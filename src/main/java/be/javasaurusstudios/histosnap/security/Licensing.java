package be.javasaurusstudios.histosnap.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a collection of common mass scan adducts to select
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class Licensing {

    /**
     * The Licensing instance
     */
    private static Licensing INSTANCE;
    /**
     * The licensing salt
     */
    private static final String SALT = "PROTEOFORMIX_2022";

    /**
     * Retrieves an instance for licensing
     *
     * @return the licensing instance
     */
    public static Licensing getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Licensing();
        }
        return INSTANCE;
    }

    private Licensing() {
        //IN LATER STAGES THIS NEEDS TO BE CHECKED ON A SERVER !
        //invalidatedKeys.add("0D92-88ADC-6F0D-ADDB7");
    }

    /**
     * List of invalidated keys (that will no longer work in this particular
     * version)
     */
    private final HashSet<String> invalidatedKeys = new HashSet<>();

    /**
     * Generates a license key for a particular user
     *
     * @param user the user's name
     * @return the key for the user
     */
    public String GenerateKey(String user) {
        String key = "";
        try {
            key = generateSerialNumber(user + SALT);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Licensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }

    public boolean IsInvalidated(String key) {
        return invalidatedKeys.contains(key.toUpperCase());
    }

    /**
     * Verifies if the provided key matches the license for the user
     *
     * @param user the user's name
     * @param key the user's key
     * @return boolean to grant access
     */
    public boolean VerifyKey(String user, String key) throws IllegalAccessError {
        boolean verified = false;
        if (IsInvalidated(key)) {
            try {
                StoreKey("", "");
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(Licensing.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                verified = generateSerialNumber(user + SALT).equalsIgnoreCase(key);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Licensing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return verified;
    }

    /**
     * Stores a key locally in a hidden file
     *
     * @param user the registered user
     * @param key the key matching the user
     * @throws URISyntaxException
     * @throws IOException
     */
    public void StoreKey(String user, String key) throws URISyntaxException, IOException {
        File licenseFile = getLicenseFile();
        if (!licenseFile.exists()) {
            licenseFile.createNewFile();
        }
        setHidden(licenseFile, false);
        FileWriter writer = new FileWriter(licenseFile, false);
        writer.append(user + ":" + key);
        writer.flush();
        setHidden(licenseFile, true);
    }

    /**
     * *
     * Reads the key from the hidden license file
     *
     * @return the serial key
     */
    public String[] ReadKey() {
        String[] value = new String[]{"", ""};
        try {
            File licenseFile = getLicenseFile();
            if (licenseFile.exists()) {
                try (BufferedReader brTest = new BufferedReader(new FileReader(licenseFile))) {
                    String line = brTest.readLine();
                    if (line != null && line.length() > 2) {
                        String storedUser = line.split(":")[0];
                        String storedKey = line.split(":")[1];
                        if (!(storedKey.isEmpty() || storedUser.isEmpty()) && VerifyKey(storedUser, storedKey)) {
                            value[0] = storedUser;
                            value[1] = storedKey;
                        }
                    }
                }
            }
        } catch (NullPointerException | IOException | URISyntaxException ex) {
            Logger.getLogger(Licensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * @return the location of the hidden license file
     * @throws URISyntaxException
     * @throws IOException
     */
    private File getLicenseFile() throws URISyntaxException, IOException {
        String licensePath = new File(Licensing.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
        File licenseFile = new File(licensePath, "proteoformix_license.lc");
        //hide the file
        if (!licenseFile.exists()) {
            licenseFile.createNewFile();
        }
        System.out.println("License file at " + licenseFile.getAbsolutePath());
        return licenseFile;
    }

    /**
     * Hides the license file
     *
     * @param licenseFile the file to hide
     * @param hide boolean indicating to show or hide the file
     */
    private void setHidden(File licenseFile, boolean hide) {
        try {
            Files.setAttribute(licenseFile.toPath(), "dos:hidden", hide, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException ex) {
            Logger.getLogger(Licensing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates a SHA1 hashed serial number
     *
     * @param fullNameString (the full user name)
     * @return a license key
     * @throws NoSuchAlgorithmException
     */
    private String generateSerialNumber(String fullNameString) throws NoSuchAlgorithmException {
        String serialNumberEncoded = calculateSecurityHash(fullNameString, "MD2")
                + calculateSecurityHash(fullNameString, "MD5")
                + calculateSecurityHash(fullNameString, "SHA1");
        String serialNumber = ""
                + serialNumberEncoded.charAt(32) + serialNumberEncoded.charAt(76)
                + serialNumberEncoded.charAt(100) + serialNumberEncoded.charAt(50) + "-"
                + serialNumberEncoded.charAt(2) + serialNumberEncoded.charAt(91)
                + serialNumberEncoded.charAt(73) + serialNumberEncoded.charAt(72)
                + serialNumberEncoded.charAt(98) + "-"
                + serialNumberEncoded.charAt(47) + serialNumberEncoded.charAt(65)
                + serialNumberEncoded.charAt(18) + serialNumberEncoded.charAt(85) + "-"
                + serialNumberEncoded.charAt(27) + serialNumberEncoded.charAt(53)
                + serialNumberEncoded.charAt(102) + serialNumberEncoded.charAt(15)
                + serialNumberEncoded.charAt(99);
        return serialNumber;
    }

    /**
     * Calculate the security hash based on the user input
     *
     * @param stringInput (the username)
     * @param algorithmName (the algorithm to apply)
     * @return a securely hashed key
     * @throws java.security.NoSuchAlgorithmException
     */
    private String calculateSecurityHash(String stringInput, String algorithmName) throws java.security.NoSuchAlgorithmException {
        String hexMessageEncode = "";
        byte[] buffer = stringInput.getBytes();
        java.security.MessageDigest messageDigest
                = java.security.MessageDigest.getInstance(algorithmName);
        messageDigest.update(buffer);
        byte[] messageDigestBytes = messageDigest.digest();
        for (int index = 0; index < messageDigestBytes.length; index++) {
            int countEncode = messageDigestBytes[index] & 0xff;
            if (Integer.toHexString(countEncode).length() == 1) {
                hexMessageEncode = hexMessageEncode + "0";
            }
            hexMessageEncode = hexMessageEncode + Integer.toHexString(countEncode);
        }
        return hexMessageEncode;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        /*   args = new String[]{
            "ProteoFormiX",
            "ProteoFormiX2",
            "ProteoFormiX3",
            "PeterVerhaert",
            "KennethVerheggen",
            "Reviewer1",
            "Reviewer2",
            "Reviewer3",
            "Reviewer4",
            "Editor"};
         */

        for (int i = 0; i < args.length; i++) {
            String user = args[i];
            String key = Licensing.getInstance().GenerateKey(user);
            System.out.println(user + ":" + key);
        }
    }

}
