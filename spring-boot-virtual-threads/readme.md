# readme


launch the [`go-httpbin` docker image ](https://github.com/mccutchen/go-httpbin) on port `9000`: 

```shell 
docker run -p 9000:8080 mccutchen/go-httpbin
```

in another shell,  set the environment variable `VT` to `true` or `false` to enable or disable the virtual threads support. 
then, launch the spring boot application:

```shell
VT=true ./mvnw spring-boot:run
```

in another shell, send some requests to the spring boot application:

```shell
hey  -c 20 -n 60 http://localhost:8080/3
```

on my machine, I get roughly the following numbers when `VT` is `false`:


```
Summary:
  Success rate: 100.00%
  Total:  12.1970 secs
  Slowest:  9.1774 secs
  Fastest:  3.0138 secs
  Average:  3.6622 secs
  Requests/sec: 4.9192

  Total data: 28.79 KiB
  Size/request: 491 B
  Size/sec: 2.36 KiB
...

```

and I get roughly the following numbers when `VT` is `true`:

```
Summary:
  Success rate: 100.00%
  Total:  9.1837 secs
  Slowest:  3.1363 secs
  Fastest:  3.0139 secs
  Average:  3.0600 secs
  Requests/sec: 6.5333

  Total data: 30.53 KiB
  Size/request: 521 B
  Size/sec: 3.32 KiB
  ...
```