#Juseppe - Jenkins Update Site Embedded for Plugin Publishing

## How to launch with help of docker
[![](https://badge.imagelayers.io/lanwen/juseppe:latest.svg)](https://imagelayers.io/?images=lanwen/juseppe:latest 'imagelayers.io')

Run it with mounted plugins folder as volume. Remember to set `JUSEPPE_BASE_URI` env var

```
docker run --name juseppe -v /your/plugins/dir/:/juseppe/plugins/ -e JUSEPPE_BASE_URI=http://my.company.com -p 80:8080 lanwen/juseppe
```

Then it will be available on `http://dockerhost:80/update-center.json`

### Built-in self-signed certificate

Certificate can be copied from json in format:

```
-----BEGIN CERTIFICATE-----
{json value of signature.certificates[0] (without quotes)}
-----END CERTIFICATE-----
```

**WARN!** Certificate regenerates in every new docker image!

### Specify own certificate

Mount as volumes private key and cert:

```
docker run --name juseppe -v /your/private/key:/juseppe/cert/uc.key -v /your/cert/file:/juseppe/cert/uc.crt ... lanwen/juseppe
```

## Build new image  

`docker build -t juseppe:source .`

# Without docker

## 1. Checkout & Build 

With maven just run `mvn package`, and you will find jar in `target/juseppe.jar`

## 2. Generate self-signed cert with private key

```
openssl genrsa -out uc.key 2048 \
&& openssl req -nodes -x509 -new \
    -key uc.key \
    -out uc.crt \
    -days 1056 \
    -subj "/C=EN/ST=Update-Center/L=Juseppe/O=Juseppe"
```
 
## 3. Run to serve plugins

To run server with file watching in current directory (not the dir where jar located!)

`java -jar juseppe.jar`

## 4. Configure 

You can define system properties to override default behaviour:

- `update.center.plugins.dir` - 
- `update.center.saveto.dir` - 
- `update.center.json.name` - 
- `update.center.release.history.json.name` - 
- `update.center.private.key` - 
- `update.center.certificate` - 
- `update.center.baseurl` - 
- `jetty.port` - 

Example: 

`java -jar -Dupdate.center.saveto.dir=/tmp/update/ juseppe.jar`

## How to connect Jenkins

Site can be added with help of: 
    
- [UpdateSites Manager plugin](https://wiki.jenkins-ci.org/display/JENKINS/UpdateSites+Manager+plugin)

