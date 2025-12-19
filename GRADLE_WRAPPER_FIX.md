# Решение проблемы с Gradle Wrapper

## Проблема
```
zsh: no such file or directory: ./gradlew
```

## Решение

Я создал файл `gradlew` и скачал `gradle-wrapper.jar`. Теперь можно использовать команды:

### ✅ Теперь доступны команды:

```bash
# Остановить Gradle daemon
./gradlew --stop

# Очистить проект
./gradlew clean

# Синхронизировать зависимости
./gradlew build --refresh-dependencies

# Собрать проект
./gradlew build
```

### ⚠️ Если все еще есть проблемы с JAVA_HOME:

Проверьте версию Java:
```bash
java -version
```

Если Java не установлена или неправильная версия, установите Java 17:
```bash
# Через Homebrew
brew install openjdk@17

# Установить JAVA_HOME (добавить в ~/.zshrc)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Или используйте Android Studio для сборки проекта (он использует встроенный JDK).

### Альтернатива: Использовать Android Studio

Если проблемы с командной строкой продолжаются, используйте Android Studio:

1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project**
3. **Build → Rebuild Project**

Android Studio использует свой встроенный JDK, поэтому проблем с JAVA_HOME не будет.

