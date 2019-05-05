#### 1. Brief introduction

The project provides a Android APP command-line development environment based on Linux system.

You can directly write、build、install Android APP without using IDE.


#### 2. How to use

Enter the root directory of an example (for example: HelloAndroid).

If it is the first time to compile, It is recommended to run `make tool` to download platform、SDK、build-tools etc（sometimes you should use sudo make tool）.

	cd ./example/HelloAndroid
	make tool

Build the project:

	make build
	
Install the APP:

	make program
	

**more：**[https://www.cnblogs.com/zjutlitao/p/9672376.html](https://www.cnblogs.com/zjutlitao/p/9672376.html)


#### 3. Watch log

Get all errors and fatals:

	adb logcat "*:E"

Use wildcard filter with Android Logcat (Linux only):

	adb logcat | grep -i "foo.example." #get all logs related to “foo.example.*” tagname


#### 4. Demos

[[01] HelloAndroid: hello world demo][#1]    
[[02] BluetoorhScan: bluetooth scan + surface(canvas) + handler + bundle][#2]    
[[03] FlyGame: surface(canvas) + fly game demo][#3]     
[[04] ListView: ListView DIY demo][#4]     
[[05] GridView: GridView DIY demo][#4]     
[[06] TuyaMeshTest: ble scan(fast scan) + textview][#5]    
[[07] SmartStepCounter: bluetooth scan connect read + line chart][#6]    
[[08] SmartFan: bluetooth scan connect write][#7]    


[#1]:https://www.cnblogs.com/zjutlitao/p/9672376.html
[#2]:https://www.cnblogs.com/zjutlitao/p/4314096.html
[#3]:https://www.cnblogs.com/zjutlitao/p/4233536.html
[#4]:https://www.cnblogs.com/zjutlitao/p/4954385.html
[#5]:https://www.cnblogs.com/zjutlitao/p/10100212.html
[#7]:https://www.cnblogs.com/zjutlitao/p/4690228.html
[#8]:https://www.cnblogs.com/zjutlitao/p/4640745.html
