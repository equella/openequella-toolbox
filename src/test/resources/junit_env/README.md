# JUNIT testing CheckFiles
CheckFiles JUnit tests require a crafted database and a folder structure to run successfully.

To modify the test data, you need to run oEQ 2019.1+ pointing it to the restored database and filestore.

All passwords are `oeq`.

mandatory-config.properties
```properties
filestore.root = {your-repo-clone}/openEQUELLA-toolbox/src/test/resources/junit_env/checkfiles/
```

optional-config.properties
```properties
filestore.advanced = true
filestore.additional.ids = 2
filestore.additional.2.path = {your-repo-clone}/openEQUELLA-toolbox/src/test/resources/junit_env/checkfiles2/oeq-filestore-2/
filestore.additional.2.name = Filestore ID#2
```

Make your changes, and then backup the database and commit the .backup file and folder structure.  