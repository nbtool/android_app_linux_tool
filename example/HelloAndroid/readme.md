
### 1、编译运行

运行会出现：

```
➜  HelloAndroid git:(master) make tool

./run.sh tool
> install sdk build-tools platform....
Error occurred during initialization of boot layer
java.lang.module.FindException: Module java.se.ee not found
make: *** [makefile:2: tool] Error 1
```

需要：

1）将 `JAVA_OPTS` 去掉：

```
function tool(){
    #mkdir some dir
    mkdir -p bin
    mkdir -p obj
    mkdir -p libs

    #export JAVA_OPTS='-XX:+IgnoreUnrecognizedVMOptions --add-modules java.se.ee'
```

2）调整环境的 java 版本

```
➜  HelloAndroid git:(master) ✗ archlinux-java status
Available Java environments:
  java-11-openjdk (default)
  java-17-openjdk
  java-21-openjdk
  java-8-jdk
  java-8-openjdk
➜  HelloAndroid git:(master) ✗ sudo archlinux-java set java-8-openjdk
[sudo] password for btfz:
➜  HelloAndroid git:(master) ✗ archlinux-java status
Available Java environments:
  java-11-openjdk
  java-17-openjdk
  java-21-openjdk
  java-8-jdk
  java-8-openjdk (default)
➜  HelloAndroid git:(master) ✗ export PATH="/usr/lib/jvm/java-8-openjdk/bin/:$PATH"
➜  HelloAndroid git:(master) ✗ make tool

```

</br>

### 2、效果

![][p1]


[p1]:./docs/pics/app_gui.png
