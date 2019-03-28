# openEQUELLA Toolbox
Proof-of-concept for a suite of scripts to interface with openEQUELLA in Java.

## Main functions
### Migrate to Kaltura
Search openEQUELLA for items with a given criteria.  For each item found, download the appropriate video, upload into Kaltura, and then re-version the openEQUELLA item, replacing the video file and other attachments with the single Kaltura attachment.

### Export Items
Search openEQUELLA for items with a given criteria.  For each item found, export the metadata in a given format.

### Email
Takes a CSV of 'to addresses', a subject, and a body and sends an email.  If 'html' is specified, it will set the email mime type to HTML.

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

# License
Apache v2

# Building

```
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


