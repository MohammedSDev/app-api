# app-api
easy api hit  using customized retrofit
 

![Release](https://jitpack.io/v/clickapps-android/app-api.svg)

<!--
MohammedSDev
-->

# App-Api is an easy,flexible library for you Java/kotlin & Android  http calls based on Retrofit2. 
you can use it for all your get, put, post, delete & download files via http/https calls in your android project,
the main feature of this library is not prevent/limit you to use you own api service,adapters,or even OkHttpClient.Builder.
this library build with love & kotlin. with fully support to java.


### add dependence
in project level `build.gradle`
```gradle 
allprojects {
		repositories {
                    google()
                    jcenter()
		                  //...
		                maven { url 'https://jitpack.io' }
		}
	}
```
in app level `build.gradle`
```gradle
dependencies {
	        implementation 'com.github.clickapps-android:app-api:last-build'
	}
```


### #How to Ues
first thing..all your response classes or just your main response class must extend from **ResponseModel** 
& your error response class must extend from **ErrorResponseModel**

## AppConfig 
`appConfig{ ... }` is function should called once before any other functions.. usuallly in *Application* class.
you can specifiy your BASE_URL,DEBUG_LEVEL,TIMEOUT,custom ADAPTERS,GENERAL ERROR MESSAGE..& more
```kotlin
appConfig {
            //BASE_URL should be end with `/`
            BASE_URL = "https://github.com/MohammedSDev/"
            //you can provide generic error message based on your user locale
            GENERAL_ERROR_MESSAGE = if(Locale.getDefault().language.equals("ar"))
                "حدث خطأ ماء, الرجاء المحاولة لاحقاً"
            else
                "Some thing went wrong,tray later"
        }

```
don't forgot to add you Application class to AndroidManifest file
```xml
...
<application
            android:name=".MainApplication"
            android:icon="@mipmap/ic_launcher"
            ...
```

# GET 
```kotlin
val yourParams = HashMap<String,String>()
yourParams["your_key"] = "your_value"
//..
get("repositories", MainResponse::class.java, MainErrorModel::class.java)
            .preRequest {
                queryParam = yourParams
                headerParam = yourHeaderParams
                this.delay = 2000
                
            }
            .onSuccess { response ->
                //do stuff.
                //e.g: setData(response)
            }
            .onError { error ->
                //do stuff
                //e.g: alertUser(error.errorMessage)
            }

            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                        //myLoader.start()
                    }
                    is AppNetworkStatus.OnSuccess -> {
                        //myLoader.hide()
                    }
                    is AppNetworkStatus.OnError -> {
                        //myLoader.error()
                    }
                    is AppNetworkStatus.OnCustom -> {
                        //myLoader.stop()
                    }
                    is AppNetworkStatus.OnCancel -> {
                        //myLoader.stop()
                    }
                }
            }
            .call()
```
# POST 

yous can use hash map or **AppParamMap** for complex params in both **bodyParam** and **queryParam**

```kotlin
 val yourParams = AppParamMap<String,Any>()
//        val yourParams = HashMap<String,String>()
yourParams["your_key"] = listOf("value1","value2","value3")
yourParams["your_key"] = "your_value"
//..
val request = post("repositories", MainResponse::class.java, MainErrorModel::class.java)
            .preRequest {
                bodyParam = yourParams
                headerParam = yourHeaderParams
                
            }
            .onSuccess { response ->
                //do stuff.
                //e.g: setData(response)
            }
            .onError { error ->
                //do stuff
                //e.g: alertUser(error.errorMessage)
            }

            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                        //myLoader.start()
                    }
                    is AppNetworkStatus.OnSuccess -> {
                        //myLoader.hide()
                    }
                    is AppNetworkStatus.OnError -> {
                        //myLoader.error()
                    }
                    is AppNetworkStatus.OnCustom -> {
                        //myLoader.stop()
                    }
                    is AppNetworkStatus.OnCancel -> {
                        //myLoader.stop()
                    }
                }
            }
            .call()


        //to cancel
        //request.cancel()
```
# PUT 
```kotlin
put("repositories", MainResponse::class.java, MainErrorModel::class.java)
            .preRequest {
                bodyParam = hashMapOf(...)
                this.delay = 2000
                
            }
            ...
            .call()
```
# DELETE 
```kotlin
delete("repositories", MainResponse::class.java, MainErrorModel::class.java)
            .preRequest {
                bodyParam = hashMapOf(...)
                this.delay = 2000
                
            }
            ...
            .call()
```
# download 
```kotlin
 val file = File.createTempFile("prefix_name","suffix")
 val fileUrl = "https://..."
 val appRequest = AppRequestParam(fileUrl)
 //appRequest.headerParam = ...
 download(file, appRequest,{
     //displayFileSize(file.length())
     //..
 },{error->
     //alertUser(error.errorMessage)
})
```
# using Java 
```java
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import static com.digital.app.api.ActionsKt.get;

...

get("categories", CategoryRes.class, ErrorModel.class)
.preRequest(new Function1<AppRequestParam, Unit>() {
    @Override
    public Unit invoke(AppRequestParam appRequestParam) {
	HashMap<String, String> p = new HashMap<>();
	p.put("locale","ar");
	appRequestParam.setQueryParam(p);
	//appRequestParam.setHeaderParam();
	return null;
    }
})
.onSuccess(new Function1<CategoryRes, Unit>() {
    @Override
    public Unit invoke(CategoryRes categoryRes) {
	System.out.println(categoryRes.getMessage());
	System.out.println(categoryRes.getCategories().get(0));
	return null;
    }
})
.onError(new Function1<ErrorModel, Unit>() {
    @Override
    public Unit invoke(ErrorModel errorModel) {
	System.out.println("categories error, ${it.errorCode}, ${it.errorMessage}");
	return null;
    }
})
.call();
```

# 
Enjoy using **app-api** library,report any bugs you found, or even drop me email gg.goo.mobile@gmail.com
