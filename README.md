# COMS-4156-Project
This is the GitHub repository for the Team Project associated with COMS 4156 Advanced Software Engineering. Our team name is TheJavaEngineers and the following are our members: Griffin, Mohsin, Jeannie, Michael, and Abenezer.

## Postman Test Documentation
View the list of API calls made over the network using post man fully documented with the received result and parameters: https://documenter.getpostman.com/view/30499865/2s9YR85tUY

## Endpoints
This section describes the endpoints that our service provides, as well as their inputs and outputs. See the
"Postman Test Documentation" section for in-depth examples of use cases and inputs/outputs, especially for file
uploads and downloads.

#### POST /register-client
* Expected Input Parameters: N/A
* Expected Output: networkId (String)
* Registers the client with the service, should be done only ONCE per the lifetime of the client. This endpoint MUST be called prior to any other calls being made. Upon success, the <code>networkId</code> is returned. Upon failure <code>"An unexpected error has occurred"</code> is returned. 

#### POST /upload-doc
* Expected Input Parameters: network-id (String), document-name (String), user-id (String), contents (MultipartFile in the request body)
* Expected Output: A String indicating the status of the upload.
* Uploads the provided document to the database. Ideally you upload a document prior to performing other api calls but it is not an issue if you do not do this, you'll just have no luck. Upon success: <code>"File uploaded successfully"</code> is returned. Upon failure: <code>"File didn't upload"</code>.

#### PATCH /share-document
* Expected Input Parameters: network-id (string), document-name (string), your-user-id (string), their-user-id (string)
* Expected Output: A String indicating the result of the operation.
* Shares the specified document with the specified user, Upon success: <code>"The document has been shared with the desired user"</code> is returned. If the specified user already has access to the document then <code>"This document has already been shared with the desired user"</code> is returned. If the calling user (represented by your-user-id) does not have access to the document then <code>"Your user does not have access to this document"</code> is returned. If the specified document is not present in the database then <code>"no such document exists"</code> is returned. If anything happens while the operations are taking place that is unexpected then <code>"An unexpected error has occurred."</code> is returned. 

#### DEL /delete-doc
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Expected Output: A String indicating the status of the deletion.
* Deletes the specified document from the client's network in the database. Upon success <code>"Your document was successfully deleted"</code> is returned. If the specified user does not have access to the document then <code>"Your user does not have ownership of this document"</code> is returned. If the specified document does not exist then <code>"no such document exists"</code> is returned. If anything happens while the operations are taking place that is unexpected then <code>"An unexpected error has occurred."</code> is returned. 

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
* Searches within the client's network on the database for the specified document. Upon success see the above for what the expected output should be. If the user does not have access to the specified document <code>"Your user does not have access to this document"</code> is returned. If the specified document does not exist then <code>"No such document exists"</code> is returned.

#### GET /see-previous-version
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String), revision-number (int)
* Expected Output: A JSON object containing the following fields:
* * clientId   (String): see description under 'GET /check-for-doc'
* * wordCount  (int): see above
* * docId      (String): see above
* * title      (String): see above
* * userId     (String): see above
* * fileString (String): see above
* Retrieves the specified previous version of a document if it is able to be retrieved. Upon success see above for details on what is returned. If the specified user does not have access to the specified document then <code>"Your user does not have access to this document"</code> is returned. If an invalid revision number is provided then <code>"This is not a valid revision number</code> is returned. If the specified document does not exist then <code>"No such document exists"</code> is returned.

#### GET /see-document-stats
* Expected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Expected Output: A String containing document information, including word count, how many users have access, and number of revisions stored.
* Retrieves common statistics about the specified doucment. Upon success see the above for the expected output. If the specified user does not have access to the document <code>"Your user does not have access to this document"</code> is returned. If the specified document does not exist then <code>"no such document exists"</code> is returned. If an unexpected error occurs then <code>"An unexpected error has occurred."</code> is returned.

#### GET /generate-difference-summary
* Expected Input Parameters: network-id (String), fst-doc-name (String representing the first document name), snd-doc-name (String representing the second document name), your-user-id (String)
* Expected Output: A String containing difference information between the two given documents, including word count difference, user count difference, and version count difference.
* Generates a summary of differences between the two specified documents, from the perspective of the first document specified, meaning if the first document has 2 users and the second document has 1 it is expected to see that first document has 1 more user than second document. Upon success see the above for the expected output. If the specified user does not have access to one or both of the specified documents then <code>"Your user does not have access to one of the documents"</code> is returned. If one of the specified documents does not exist within the client's network then <code>"One or more of the documents does not exist"</code> is returned. If an unexpected error occurs then <code>"An unexpected error has occurred."</code> is returned. 

#### GET /download-doc
* Exoected Input Parameters: network-id (String), document-name (String), your-user-id (String)
* Optional Input Body: A JSON string containing a document to be downloaded.
* Expected Output: HTTP OK Status along with the raw text contents of the document in the response body.
* Retrieves the raw contents of the document and returns it with <code>HTTP OK Status</code> upon success. If a JSON body is provided it it takes precedent and the other parameters do not matter. If the JSON body is present and malformed then an <code>HTTP BAD_REQUEST Status</code> is returned along with <code>"The request body is malformed"</code> in the response body. The following assumes no JSON body is present in the HTTP request and some failure occurs: If the specified user does not have access to the specified document then <code>HTTP FORBIDDEN Status</code> is returned along with <code>"You do not have ownership of this document"</code> in the response body. If the specified document does not exist then <code>HTTP NOT_FOUND Status</code> is returned along with <code>"No such document exists"</code> in the response body. If an unexpected error occurs then <code>HTTP INTERNAL_SERVER_ERROR Status</code> is returned along with <code>"An unexpected error has occurred"</code> in the response body.

---

## Tools used
This section includes notes on tools and technologies used in building this project, as well as any additional details if applicable.

* Firebase DB
* Maven Package Manager
* GitHub Actions CI
  * This is enabled via the "Actions" tab on GitHub.
  * Currently, this just runs a Maven build to make sure the code builds on branch 'main'.
* Checkstyle
  * We use Checkstyle for code reporting. Note that Checkstyle does NOT get run as part of the CI pipeline.
  * For running Checkstyle manually, you can use the "Checkstyle-IDEA" plugin for IntelliJ.
* SonarQube
  * TODO: Will do static analysis with SonarQube in second iteration.
* JUnit
  * JUnit tests get run automatically as part of the CI pipeline.
* Cobertura
  * We use Cobertura for generating code coverage reports.
* Postman
  * We used Postman for testing that the APIs work.

## Building and Running
You can build our project using the build command in IntelliJ using Java 17. From there, you can hit the "Run" button to start
the service.

Our endpoints are listed below in the "Endpoints" section, with brief descriptions of their parameters. For in-depth examples and system-level
tests of them, see the section "Postman Test Documentation" below.

## Running Tests
Our unit tests are located under the directory 'src/test'. To run our project's tests in IntelliJ using Java 17, you must first build the project.

From there, you can right-click the 'DocumentTest' class from the IntelliJ Project View and click "Run 'DocumentTest'".
Next, you can do the same thing with 'FirebaseServiceTest' to run the tests of the Firebase service.

To see our system-level tests, see the section "Postman Test Documentation" below.

## Style Checking Report
We used the tool "checkstyle" to check the style of our code and generate style checking reports. Here is the report
as of the day of 10/19/23:

![Screenshot of a checkstyle report for our project, showing 0 warnings and errors](screenshots/checkstyle-report.png)
![Screenshot of another checkstyle report for our project, showing 0 warnings and errors](screenshots/checkstyle.png)
![Screenshot of a checkstyle report from the plugin, showing 0 warnings and errors](screenshots/checkstyle-plugin.png)
