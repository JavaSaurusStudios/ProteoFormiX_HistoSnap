<p align="center"> 
  <img src="https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/Logo.png" />
</p>

# Welcome to HistoSnap
  
HistoSnap is a graphical user interface designed to rapidly extract an image from complex, large mass spectrometry based imaging. If you want to learn more about HistoSnap, please read the following tutorial ! We encourage you to download the latest release and play around.

# Introduction

Histochemistry comprises a collection of physicochemical techniques to generate images of cells and tissues. Histochemistry enables the localization of peptides, proteins, metabolites and other (bio)molecules in situ.
Traditionally, various biomolecules are visualized by light microscopical or fluorescence labelling. We have developed MSHC (mass spectrometry histochemistry) as an alternative, based on MS imaging (MSI). MSHC reveals the spatial distribution of molecules in sections of biological tissue, by accurately measuring their molecular masses.
Depending on the spatial resolution and mass spectrometric analysis 'depth', MSHC datasets can easily grow beyond 5 GB. Filtering the biologically relevant information out of these big datasets represents a great challenge. Here we describe a simple software tool to extract m/z based images from complex MSHC datasets.

HistoSnap is an open source graphical user interface implementation created in Java 8 and Python 3. The backend employs the pyimzML library, enabling the parsing of large datasets. An intuitive graphical interface allows users to customize the visualization (such as pixel scale, color range, etc.), without having to go through complex set up steps that typically are associated in other (commercial) software. Users are enabled to export images based on the m/z value of molecules as individual frames (PNG) or as animation (GIF), with the option to highlight a region of interest.

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_view.png?raw=true, "Full screenshot of the tool in action")

# User Manual

## Prerequisites
	* Java 8 (minimal)
	* Python3
	* Pip
	* [pyimzML](https://github.com/alexandrovteam/pyimzML)

## Loading a file

When initially starting up HistoSnap, you will notice the input bar is red. Prior to any operation, an imzML file needs to be selected. Please do ensure the corresponding idb file is present and has the exact same name, including capitalisation! A file can be selected by using the Load button under the File menu.

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_loading_1.png?raw=true, "Loading a file")

After selecting a valid file, the input bar will turn green and operations can be executed !

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_loading_2.png?raw=true, "A file was loaded")

## Extracting one (or multiple) images

The extract menu provides several options to control what mass range(s) will be considered to extract an image. 

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_extract_0.png?raw=true, "Extracting images")

### Extracting the image background

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_extract_1.png?raw=true, "Extracting a background image")

A background image is selected by randomly selecting mass ranges according to the specifications provided by the user and randomizing them : 
   
| Parameter  | Function |
| ------------- | ------------- |
| Samples  | The amount of random images to select  |
| Minimal deviation  | The mz bin size (mass tolerance)  | 
| Minimal mz  | The lower mz limit  | 
| Maximal mz  | The upper mz limit  | 

### Extract a simple image

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_extract_2.png?raw=true, "Extracting a single image")

In the simplest case, a single image can be generated by specifying two boundary values

| Parameter  | Function |
| ------------- | ------------- |
| Minimal mz  | The lower mz limit  | 
| Maximal mz  | The upper mz limit  | 

### Extract a sequence of images

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_extract_3.png?raw=true, "Extract multiple images")

It is possible to generate a sequence of consecutive images

| Parameter  | Function |
| ------------- | ------------- |
| Minimal mz  | The lower mz limit  | 
| Maximal mz  | The upper mz limit  | 
| Steps  | The amount of bins that need to be   | 

### Extract a list of mz-values

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_extract_4.png?raw=true, "Extract images based on a list of mz values")

It is possible to generate a sequence of consecutive images

| Parameter  | Function |
| ------------- | ------------- |
| Minimal deviation  | The mz bin size (mass tolerance)  | 
| List of masses  | A list of mz values of interest (one per line)  | 


## Operations on generated images

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_right_click.png?raw=true, "Potential operations")

By selecting multiple images in the list (left) and right clicking, a pop up menu allows access to certain operation

| Operation  | Function |
| ------------- | ------------- |
| Delete  | Deletes the selected images  | 
| Rename  | Renames the selected images  | 
| Combine  | Combines the selected images  | 
| Save animation  | Creates an animated GIF for the selected images  | 
| Save Frame(s)  | Saves the selected images as individual images  | 
| Check Similarities  | A feature to automatically select images that derive from a specified background (experimental)  | 

## Additional Options

### Colors

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_0.png?raw=true, "Heatmap colors")

The color scale that will be applied to the generated images (heatmap styles).

### Scale

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_1.png?raw=true, "Pixel Scales")

The pixel scale

### Intensity Mode

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_2.png?raw=true, "Intensity Mode")

The intensity mode represents the statistic which will be applied as a reference for the pixel intensity. In other words, the intensity of a pixel's color is correlated to the selected statistical parameter.

### Adducts

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_3.png?raw=true, "Anion adducts")

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_4.png?raw=true, "Cation adducts")

Adducts can be selected (both cations and anions) to be considered. These additional masses will be scanned seperately from the input mass to charge value.

### System and Memory options

![alt text](https://github.com/JavaSaurusStudios/ProteoFormiX_HistoSnap/blob/main/src/main/resources/screenshots/histosnap_options_5.png?raw=true, "Memory options")

Processing imagine data requires a large amount of memory. To combat this, HistoSnap has two modes : Low Memory and High Memory. 

* Low Memory Mode avoids storing most data in memory by using a local database, which will be generated in the same folder as the input file. The trade-off is that this mode is slower.
* High Memory Mode loads the spectra in memory, allowing for faster processing. However, running in memory mode without having adequate amounts of RAM available will cause the software to hang.

