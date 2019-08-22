# openEQUELLA Toolbox
A Java-based suite of scripts geared to work with openEQUELLA.

## Main functions

### Migrate to Kaltura
Search openEQUELLA for items with a given criteria.  For each item found, download the appropriate video, upload into Kaltura, and then re-version the openEQUELLA item, replacing the video file and other attachments with the single Kaltura attachment.

### Export Items
Search openEQUELLA for items with a given criteria.  For each item found, export the metadata in a given format.

### Email
Invocation arguments:
* [req] config file
* [req] html or nohtml // If 'html' is specified, it will set the email mime type to HTML.
* [req] CSV of 'to addresses'
* [req] email subject
* [req] email body

### FileLister
Lists in JSON format all files / directories of a given directory.

When used in an openEQUELLA save script, it's helpful to change the log4j console appender to `%msg%n` so only the JSON is returned.

Invocation arguments:
* [req] config file
* [req] directory (or file) to list
* [opt] -useParent flag

### Thumbnail
Using similar techniques as openEQUELLA, thumbnails a file and provides diagnostics about the thumbnail process.

Invocation arguments:
* [req] config file
* [req] file to thumbnail

### CheckFiles
Crawls through an openEQUELLA installation and confirms if attachments that are in the DB attachments table exist in the filestore.

Note:  This *cannot* check files that are in an item's persistent folder, but not in the DB as an attachment.

For full details on configuration, please see `blank.properties`.

Invocation arguments
*Standard CheckFiles run:*
* [req] config file

*Ad-hoc comparison of two reports:*
* [req] config file
* [req] -compare
* [req] csv-report1.csv
* [req] csv-report2.csv

### AttachmentHash
Creates the parent directory of a given attachment hash in the openEQUELLA filestore.

Invocation arguments
*Standard CheckFiles run:*
* [req] config file
* [req] attachment uuid hash

# License
Apache v2

# Building

1. Inflate `src/test/resources/junit_env.zip`
2. Restore the DB from the zip
3. Copy the primary and secondary filestores into your test install
4. Fill in the properties in `conf/junit.properties`.
5. Ensure the following setup is in place in `optional-config.properties`
```properties
filestore.advanced = true
filestore.additional.ids = 2,3
filestore.additional.2.path = {your-path}/adv-filestores/beta/
filestore.additional.2.name = Filestore Beta
filestore.additional.3.path = {your-path}/adv-filestores/charlie/
filestore.additional.3.name = Filestore Charlie
``` 
6. Run the build
```sh
./gradlew clean build
```

Grab the artifact from `build/libs`

## To build with the Kaltura functions:

Add into `build.gradle > dependencies`

```
compile group: 'com.kaltura', name: 'kalturaApiClient', version: '1.0.3'
	
```

Uncomment everything to do with Kaltura (this needs to be fixed).

# Running

```
java -Dlog4j.configurationFile=log4j2.xml -jar openequella-toolbox-<<version>>.jar <<properties file>> <<invocation specific arguments>>
```


