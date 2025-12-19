# Быстрое решение проблем

## ✅ Проблема 1: gradlew не найден - РЕШЕНО
```bash
chmod +x gradlew
```

## ✅ Проблема 2: JAVA_HOME неверный - РЕШЕНО
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Для постоянного исправления добавьте в `~/.zshrc`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

## ✅ Проблема 3: Зависимости не разрешаются - РЕШЕНО
- Добавлен репозиторий JitPack в `settings.gradle.kts`
- Удален лишний файл `google-services (5).json`

## 🔄 Проблема 4: Ошибки компиляции

### Решение через Android Studio (рекомендуется):

1. **File → Invalidate Caches / Restart**
2. **File → Sync Project with Gradle Files**
3. **Build → Clean Project**
4. **Build → Rebuild Project**

### Решение через терминал:

```bash
# Установить JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Очистить проект
./gradlew clean

# Собрать проект
./gradlew assembleDebug
```

## 📝 Итоговые команды для запуска

Создайте файл `build.sh` в корне проекта:

```bash
#!/bin/bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew "$@"
```

Сделайте исполняемым:
```bash
chmod +x build.sh
```

Используйте:
```bash
./build.sh clean
./build.sh assembleDebug
./build.sh build
```

## ⚠️ Важно

Всегда устанавливайте JAVA_HOME перед запуском gradlew:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Или добавьте в `~/.zshrc` для автоматической установки.





