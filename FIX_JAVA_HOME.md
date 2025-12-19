# Исправление проблемы с JAVA_HOME

## Проблема
```
ERROR: JAVA_HOME is set to an invalid directory: /opt/homebrew/opt/openjdk@24
```

## Решение

### Вариант 1: Временное исправление (для текущей сессии терминала)

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Затем выполните команды gradlew:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Вариант 2: Постоянное исправление (рекомендуется)

Добавьте в файл `~/.zshrc` (или `~/.bash_profile` для bash):

```bash
# Установка JAVA_HOME для Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Затем выполните:
```bash
source ~/.zshrc
```

### Вариант 3: Использование Android Studio

Android Studio использует встроенный JDK, поэтому проблемы с JAVA_HOME не будет, если работать через IDE.

## Проверка

После установки JAVA_HOME проверьте:

```bash
echo $JAVA_HOME
# Должно показать: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

java -version
# Должно показать: openjdk version "17.0.17"

./gradlew --version
# Должно показать версию Gradle без ошибок
```

## Если проблема сохраняется

1. Проверьте, что Java 17 установлена:
   ```bash
   /usr/libexec/java_home -V
   ```

2. Если Java 17 не установлена, установите через Homebrew:
   ```bash
   brew install openjdk@17
   ```

3. Убедитесь, что в `app/build.gradle.kts` указана правильная версия:
   ```kotlin
   compileOptions {
       sourceCompatibility = JavaVersion.VERSION_17
       targetCompatibility = JavaVersion.VERSION_17
   }
   ```
