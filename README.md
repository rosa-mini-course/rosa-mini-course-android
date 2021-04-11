# rosa-mini-course-android

## 更新 schema.json 的方法

```cmd
.\gradlew downloadApolloSchema --endpoint="http://localhost:4000/graphql" --schema="app/src/main/graphql/com/winnerwinter/schema.json"
```


## 试用了 Kotlin JVM IR Backend

参考资料: https://blog.jetbrains.com/kotlin/2021/02/the-jvm-backend-is-in-beta-let-s-make-it-stable-together/

```groovy
compileKotlin {
    kotlinOptions.useIR = true
}
```

如果出了问题可以考虑取消试用。

