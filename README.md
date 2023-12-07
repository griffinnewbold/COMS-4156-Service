# COMS-4156-Project
This is the GitHub repository for the **service portion** of the Team Project associated with COMS 4156 Advanced Software Engineering. Our team name is TheJavaEngineers and the following are our members: Griffin, Mohsin, Jeannie, Michael, and Abenezer.

## Viewing the Client App Repository
Please use the following link to view the repository relevant to the app: https://github.com/griffinnewbold/COMS-4156-App

## Building and Running a Local Instance
In order to build and use our service you must install the following (This guide assumes Windows but the Maven README has instructions for both Windows and Mac):

1. Maven 3.9.5: https://maven.apache.org/download.cgi Download and follow the installation instructions, be sure to set the bin as described in Maven's README as a new path variable by editing the system variables if you are on windows or by following the instructions for MacOS.
2. JDK 17: This project used JDK 17 for development so that is what we recommend you use: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
3. IntelliJ IDE: We recommend using IntelliJ but you are free to use any other IDE that you are comfortable with: https://www.jetbrains.com/idea/download/?section=windows
4. When you open IntelliJ you have the option to clone from a GitHub repo, click the green code button and copy the http line that is provided there and give it to your IDE to clone.
5. That should be it in order to build the project with maven you can run <code>mvn -B package --file pom.xml</code> and then you can either run the tests via the test files described below or the main application by running SweProjectApplication.java from your IDE.
6. If you wish to run the style checker you can with <code>mvn checkstyle:check</code> or <code>mvn checkstyle:checkstyle</code> if you wish to generate the report. 

Our endpoints are listed below in the "Endpoints" section, with brief descriptions of their parameters. For in-depth examples and system-level
tests of them, see the section "Postman Test Documentation" below.

## Running a Cloud based Instance
For a short time you'll be able to reach our service by the magic of cloud computing here is what you need to do: 
1. When running tests in Postman point them to:  http:// 34.86.161.172 /endpoint
2. For example and to see if the cloud service is still operational please see if the following displays "Connection is Successful" http://34.86.161.172/test-connectivity
3. If the above produced "Connection is Successful" that means the service is operational via the cloud, you are still welcome to make your own instance locally as per the instructions above!
4. Happy Hacking!

## Running Tests
Our unit tests are located under the directory 'src/test'. To run our project's tests in IntelliJ using Java 17, you must first build the project.

From there, you can right-click any of the classes present in the src/test directory and click run to see the results.

To see our system-level tests, see the section "Postman Test Documentation" below.

## Postman Test Documentation
View the list of API calls made over the network using post man fully documented with the received result and parameters: [https://documenter.getpostman.com/view/30499865/2s9YR85tUY](https://documenter.getpostman.com/view/30499865/2s9YeHbBW9)

Specific Tests confirming proper returning of HTTP status codes and content types are ran during Continuous Integration as well.

## Endpoints
This section describes the endpoints that our service provides, as well as their inputs and outputs. See the
"Postman Test Documentation" section for in-depth examples of use cases and inputs/outputs, especially for file
uploads and downloads. To see more information on the method level specific api via javadoc and also to access what exceptions could be thrown visit: [https://griffinnewbold.github.io/javaindex ](https://griffinnewbold.github.io/servicejavadoc/index.html)
Any malformed request such that there is an error in your wording i.e. you do not use %20 for a space, or such that the API endpoint structure does not match what you are attempting to send you will receive a <code>HTTP 400 Bad Request</code> in response.

#### POST /register-client
* Expected Input Parameters: N/A
* Expected Output: networkId (String)
* Registers the client with the service, should be done only ONCE per the lifetime of the client. This endpoint MUST be called prior to any other calls being made.
* Upon Success: HTTP 200 Status Code is returned along with the networkID in the response body
* Upon Failure: HTTP 500 Status Code is returned along with "An unexpected error has occurred" in the response body. 

#### POST /upload-doc
* Expected Input Parameters: network-id (String), document-name (String), user-id (String), contents (MultipartFile in the request body)
* Expected Output: A String indicating the status of the upload.
* Uploads the provided document to the database. Ideally you upload a document prior to performing other api calls but it is not an issue if you do not do this, you'll just have no luck.
* Upon Success: HTTP 200 Status Code is returned along with either "File Uploaded Successfully" or "File already exists!" in the response body.
* Upon Failure: HTTP 500 Status Code is returned along with "File didn't upload" in the response body.

#### PATCH /share-document
* Expected Input Parameters: network-id (string), document-name (string), your-user-id (string), their-user-id (string)
* Expected Output: A String indicating the result of the operation.
* Shares the specified document with the specified user.
* Upon Success: HTTP 200 Status Code is returned along with either "The document has been shared with the desired user" or "This document has already been shared with the desired user" in the response body.
* Upon Failure:
   * HTTP 403 Status Code with "User does not have access to this document!" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "No such document exists." If the specified document does not exist.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### DEL /delete-doc
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Expected Output: A String indicating the status of the deletion.
* Deletes the specified document from the client's network in the database.
* Upon Success: HTTP 200 Status Code is returned along with "Your document was successfully deleted" in the response body.
* Upon Failure:
   * HTTP 403 Status Code with "Your user does not have ownership of this document" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "No such document exists." If the specified document does not exist.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### GET /check-for-doc
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Expected Output: A JSON object containing the following fields:
* * clientId (string): the network-id
* * wordCount (integer): the word count of the document
* * docId (string): a unique document ID
* * title (string): the document-name
* * userId (string): the users with access to this document, represented as a string with '/' characters separating individual user IDs
* * fileString (string): The Base64 encoded document text.
* * previousVersions (list): A JSON list of previous versions of the document, with each list entry containing the following fields:
* * * clientId (string): see above
* * * wordCount (integer): see above
* * * docId (string): see above
* * * title (string): see above
* * * userId (string): see above
* * * fileString (string): see above
* Searches within the client's network on the database for the specified document.
* Upon Success: HTTP 200 Status Code is returned along with the document contents in a JSON structure.
* Upon Failure:
   * HTTP 403 Status Code with "Your user does not have ownership of this document" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "No such document exists." If the specified document does not exist.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### GET /see-previous-version
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String), revision-number (int)
* Expected Output: A JSON object containing the following fields:
* * clientId   (String): see description under 'GET /check-for-doc'
* * wordCount  (int): see above
* * docId      (String): see above
* * title      (String): see above
* * userId     (String): see above
* * fileString (String): see above
* Retrieves the specified previous version of a document if it is able to be retrieved.
* Upon Success: HTTP 200 Status Code is returned along with the document contents in a JSON structure.
* Upon Failure:
   * HTTP 403 Status Code with "Your user does not have ownership of this document" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "No such document exists." If the specified document does not exist.
   * HTTP 400 Status Code with "This is not a valid revision number" in the response body.


#### GET /see-document-stats
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Expected Output: A String containing document information, including word count, how many users have access, and number of revisions stored.
* Retrieves common statistics about the specified doucment.
* Upon Success: HTTP 200 Status Code is returned along with the document statistics.
* Upon Failure:
   * HTTP 403 Status Code with "Your user does not have ownership of this document" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "No such document exists." If the specified document does not exist.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### GET /generate-difference-summary
* Expected Input Parameters: network-id (String), fst-doc-name (String representing the first document name), snd-doc-name (String representing the second document name), your-user-id (String)
* Expected Output: A String containing difference information between the two given documents, including word count difference, user count difference, and version count difference.
* Generates a summary of differences between the two specified documents, from the perspective of the first document specified, meaning if the first document has 2 users and the second document has 1 it is expected to see that first document has 1 more user than second document.
* Upon Success: HTTP 200 Status Code is returned along with the difference summary.
* Upon Failure:
   * HTTP 403 Status Code with "Your user does not have access to one of the documents" in the body if the specified user does not have access to the specified document
   * HTTP 404 Status Code with "One or more of the documents does not exist" If the specified document does not exist.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

  
#### GET /download-doc
* Exoected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Optional Input Body: A JSON string containing a document to be downloaded.
* Expected Output: HTTP OK Status along with the raw text contents of the document in the response body.
* Retrieves the raw contents of the document.
* Upon Success: HTTP 200 Status Code is returned along with the contents of the document requested.
* Upon Failure:
   * HTTP 403 Status Code with "You do not have ownership of this document" in the body if the specified user does not have access to the specified document
   * HTTP 400 Status Code with "The request body is malformed" If the optional body is malformed.
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

## Additions Since First Iteration
We found that as we were developing the client app that there were some features that we needed that we didn't have already implemented so the additional functionality was provided
so we have the full scope of our revised proposal AND more. The javadoc link has also been updated to reflect the latest changes. 

#### GET /retrieve-docs
* Expected Input Parameters: network-id (String), user-id (String)
* Expected Output: HTTP OK Status along with a JSON string containing all documents that contain user-id
* Retrieves all documents that contain the specified user-id
* Upon Success: HTTP 200 Status Code is returned along with a JSON string containing all documents that contain user-id
* Upon Failure:
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### GET /retrieve-doc-names
* Expected Input Parameters: network-id (String), user-id (String)
* Expected Output: HTTP OK Status along with a JSON string containing all documents names for which the document contains user-id
* Retrieves all document names for which the document contains user-id
* Upon Success: HTTP 200 Status Code is returned along with a JSON string containing all documents names for which the document contains user-id
* Upon Failure:
   * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

## Style Checking Report
We used the tool "checkstyle" to check the style of our code and generate style checking reports. Here is the report
as of the day of 11/30/23 (These can be found in the reports folder):

![Screenshot of a checkstyle with no errors](reports/checkstyle-report1-november30.png)
![Second Screenshot of a checkstyle with no errors](reports/checkstyle-report2-november30.png)

## Branch Coverage Reporting
We used JaCoCo to perform branch analysis in order to see the branch coverage of the relevant code within the code base. See below
for screenshots demonstrating output.

![Screenshot of a code coverage report from the plugin](reports/codecoverage-report1-november30.png)

## Static Code Analysis
We used PMD to perform static analysis on our codebase, see below for the most recent output. 

![Screenshot of PMD analysis report](reports/static_analysis_report_december1.png)

A report of from November 30 is present but is outdated as the image above is from December 1st. We made the decision to not include issues from priority levels 4 and 5 as PMD states (https://pmd.github.io/pmd/pmd_userdocs_configuring_rules.h) these are medium-low
and low risks respectively. All of the Priority 5 issues that were displayed on the full report were false positives at best and disgruntled differences on variable placement at worst and has no effect on the execution of the service, while priority 4 was similar these
were relatively few in number such that they were fixed.

## Continuous Integration Report
This repository using GitHub Actions to perform continous integration, to view the latest results go to the following link: https://github.com/griffinnewbold/COMS-4156-Service/actions/workflows/maven.yml

Click on the latest job on the top under "X workflow runs" then Click 'build' under jobs finally click the drop down next to all the action items to read the logs made during their execution

For convenience a recent CI Report has been manually created and is available for view in the reports directory under the appropriately named file.

## An Initial Note to Developers
If you are reading this then you would consider yourself a developer and you may also have a slight interest in developing your own app that uses the service provided in the given repository. Well in order to do so you would need to develop a project with a backend 
capable of communicating with our API, many languages offer different libraries capable of making HTTP requests of different kinds. You'd want to make sure that you are hosting the service on your own hardware/server rather than relying on any of the non local resources that may influence the results of your own app, this is especially true since our project does not utilize docker containers. But you while you could clone this repository into the same directory as your app and make a monlith, we'd personally recommend
against it. It can lead to confusion on what resources you are working with and need to update especially for more novice developers, separate directories make the process much simplier. If you wish to continue with the process of developing an app, please view the instructions for building, testing, and running a local instance above, and then check out the sister repository linked above and view it for how a client app could look that makes use of this service, finally view the section of the README titled: "A Final Note to Developers". An important note for both the service and client app, since they both rely on Firebase as the persistent storage option, you will need to provide your own configuration file in the directory containing the build file, for maven it would be the pom.xml file. To make your own database and config file, go to https://console.firebase.google.com/ and create your own project. Upon creation, navigate to Project Settings > Service Accounts and click "Generate a new private key" the file that downloads will be the file needed, it should be named "firebase_config.json" for proper use with our service, you should alter the value provided as a parameter in <code>FirebaseConfig.java</code> in the setDatabaseUrl() method and pass in the link to your database rather than ours assuming you wish to have your own separate database instance for your version of the service. 

## Tools used 🧰
This section includes notes on tools and technologies used in building this project, as well as any additional details if applicable.

* Firebase DB 
* Maven Package Manager
* GitHub Actions CI
  * This is enabled via the "Actions" tab on GitHub.
  * Currently, this just runs a Maven build to make sure the code builds on branch 'main'.
* Checkstyle
  * We use Checkstyle for code reporting. Note that Checkstyle does NOT get run as part of the CI pipeline.
  * For running Checkstyle manually, you can use the "Checkstyle-IDEA" plugin for IntelliJ.
* PMD
  * We are using PMD to do static analysis of our Java code.
  * Originally we were planning on using SonarQube, however we did not do this as it requires us to either pay or setup a server to host a SonarQube instance.
* JUnit
  * JUnit tests get run automatically as part of the CI pipeline.
* JaCoCo
  * We use JaCoCo for generating code coverage reports.
  * Originally we were planning on using Cobertura, however Cobertura does not support our version of Java.
* Postman
  * We used Postman for testing that the APIs work.

## Third Party API Documentation
We made extensive use of Firebase Realtime Database provided by Google for both the service portion and the app portion of the project, here is a link to the full 
documentation provided by Google on how to use the different components provided and gives insight into how we managed the development of this project while making use of it: https://firebase.google.com/docs/database/
