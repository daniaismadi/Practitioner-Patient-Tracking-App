# Project

FIT3077 Project Repository

Please use Intelli-J to run the application.
1. In order to run the application, you have to install the relevant dependencies as specified in pom.xml (MongoDB 
and JSON).
2. You also have to install MongoDB (the community edition) into your computer. Follow the installation instructions 
for your device here: https://docs.mongodb.com/manual/installation/.
3. Once everything is correctly configured, run the application by running the main function in RunApp class inside the 
View package.
4. All videos (System Demonstration for Assignment 2 and 3) are in src/videos/ folder and all documents 
(Class Diagram and Design Rationale for Assignment 2 and 3) are in the src/documents/ folder.
5. Make sure to mark the Java directory as the source root. (This can be done in File-> Project Structure-> Module)
6. The pom.xml file must have a maven build in order to be able to run the files.

Note: Before logging in with Practitioner ID, you will have the option to fetch new encounters and/or fetch new
observations for this practitioner by selecting the appropriate checkboxes. If none of these options are selected, the
system will just retrieve patient information from the local database. However, if the practitioner ID does not exist 
in the database yet, the system will automatically fetch new encounters and observations anyways regardless of whether
the checkboxes have been selected or not.
