Juseppe - Jenkins Update Site Embedded for Plugin Publishing
====================

## Getting started

### 1. Checkout & Build 

With maven just run `mvn package`, and you will find jar in `target/juseppe-{version}-jar-with-dependencies.jar`
 
### 2. Run to serve plugins

To run server with file watching in current directory (not the dir where jar located!)

`java -jar juseppe-{version}-jar-with-dependencies.jar`

### 3. Configure 

You can define system properties to override default behaviour:

- `update.center.plugins.dir` - where the plugins are. Searches only `*.hpi`. Defaults to *current working dir*
- `update.center.saveto.dir` - where to save generated json file. Defaults to *current working dir*
- `update.center.json.name` - name of generated json file. Defaults to `update-center.json`
- `update.center.baseurl` - url to prepend for plugins download link in json. Defaults to `http://localhost:8080`
- `jetty.port` - port for file server. Defaults to `8080`

Example: 

`java -jar -Dupdate.center.saveto.dir=/tmp/update/ juseppe-{version}-jar-with-dependencies.jar`
