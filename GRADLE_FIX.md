# Решение ошибки Gradle: Could not resolve all files

## Проблема
```
Caused by: org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration$ArtifactResolveException: 
Could not resolve all files for configuration ':app:debugCompileClasspath'.
```

## Решения (выполняйте по порядку)

### ✅ Решение 1: Удалить лишний файл google-services.json

У вас есть два файла `google-services.json`. Удалите файл с "(5)" в названии:
- ❌ `app/google-services (5).json` - удалить
- ✅ `app/google-services.json` - оставить

### ✅ Решение 2: Добавить репозиторий JitPack

Я уже добавил репозиторий JitPack в `settings.gradle.kts` для библиотеки MPAndroidChart.

### ✅ Решение 3: Очистить кэш Gradle

Выполните в терминале Android Studio или в корне проекта:

```bash
# Очистка проекта
./gradlew clean

# Очистка кэша Gradle
./gradlew --stop

# Удаление кэша (опционально, если не помогло)
rm -rf ~/.gradle/caches/
```

Или в Android Studio:
1. **File → Invalidate Caches / Restart**
2. Выберите **"Invalidate and Restart"**

### ✅ Решение 4: Синхронизировать проект

В Android Studio:
1. **File → Sync Project with Gradle Files**
2. Дождитесь завершения синхронизации

### ✅ Решение 5: Проверить интернет-соединение

Gradle должен иметь доступ к:
- `https://dl.google.com` (Google репозиторий)
- `https://repo1.maven.org` (Maven Central)
- `https://jitpack.io` (JitPack)

### ✅ Решение 6: Обновить версии (если не помогло)

Если проблема сохраняется, попробуйте обновить версии в `build.gradle.kts`:

```kotlin
// В корневом build.gradle.kts
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false  // Обновлено
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

## Проверка после исправлений

1. Убедитесь, что в папке `app/` только один файл `google-services.json`
2. Выполните **File → Sync Project with Gradle Files**
3. Проверьте, что нет ошибок в консоли Gradle

## Если проблема сохраняется

Выполните команду для детальной диагностики:

```bash
./gradlew build --stacktrace --info
```

Это покажет подробную информацию о том, какая именно зависимость не может быть разрешена.

## Типичные причины

| Причина | Решение |
|---------|---------|
| Отсутствует репозиторий JitPack | Добавлен в settings.gradle.kts |
| Лишний google-services.json | Удалить файл с "(5)" |
| Кэш Gradle поврежден | Очистить кэш (Решение 3) |
| Нет интернета | Проверить соединение |
| Неправильная версия плагина | Обновить версии |

## После исправления

После выполнения всех шагов проект должен успешно синхронизироваться. Если проблема сохраняется, пришлите полный лог ошибки из консоли Gradle.





