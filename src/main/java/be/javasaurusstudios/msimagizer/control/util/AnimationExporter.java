package be.javasaurusstudios.msimagizer.control.util;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

/**
 * 
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class AnimationExporter implements Closeable {

    // writer to export gif animations
    protected ImageWriter gifWriter;
    //parameters for the creation of the gif
    protected ImageWriteParam imageWriteParam;
    //metadata for the created gif
    protected IIOMetadata imageMetaData;

    /**
     * Constructor
     * @param outputStream The outputstream for the animation
     * @param imageType the time of image that acts as input
     * @param timeBetweenFramesMS The delay between frames
     * @param loopContinuously boolean indicating if this gif is looping
     * @throws IIOException
     * @throws IOException 
     */
    public AnimationExporter(
            ImageOutputStream outputStream,
            int imageType,
            int timeBetweenFramesMS,
            boolean loopContinuously) throws IIOException, IOException {
        // my method to create a writer
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier
                = ImageTypeSpecifier.createFromBufferedImageType(imageType);

        imageMetaData
                = gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                        imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(
                root,
                "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "transparentColorFlag",
                "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "delayTime",
                Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute(
                "transparentColorIndex",
                "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = getNode(
                root,
                "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        gifWriter.setOutput(outputStream);

        gifWriter.prepareWriteSequence(null);
    }

    /**
     * Appends an image to the sequence
     * @param img the new frame to be exported into the animation
     * @throws IOException 
     */
    private void writeToSequence(RenderedImage img) throws IOException {
        gifWriter.writeToSequence(
                new IIOImage(
                        img,
                        null,
                        imageMetaData),
                imageWriteParam);
    }

    @Override
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }

    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    private static IIOMetadataNode getNode(
            IIOMetadataNode rootNode,
            String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
                    == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }

    /**
     * Saves a set of images into a looping gif
     * @param images the input images
     * @param outputFile the file location to store the animation
     * @param ms frame delay (in milliseconds)
     * @param looping boolean indicating if the gif loops
     * @throws Exception 
     */
    public static void Save(BufferedImage[] images, File outputFile, int ms, boolean looping) throws Exception {

        try (ImageOutputStream output = new FileImageOutputStream(outputFile); AnimationExporter writer = new AnimationExporter(output, images[0].getType(), ms, looping)) {
            // write out the first image to our sequence...
            writer.writeToSequence(images[0]);
            for (int i = 1; i < images.length - 1; i++) {
                writer.writeToSequence(images[i]);
            }
        }
    }

}
