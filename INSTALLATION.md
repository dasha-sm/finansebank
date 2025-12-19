# Инструкция по установке и настройке

## Предварительные требования

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17 или выше
- Android SDK с минимальной версией API 26 (Android 8.0)
- Аккаунт Firebase (для синхронизации данных)

## Пошаговая установка

### 1. Клонирование проекта

```bash
git clone <repository-url>
cd finanse-mdk
```

### 2. Настройка Firebase

#### 2.1. Создание проекта Firebase

1. Перейдите на [Firebase Console](https://console.firebase.google.com/)
2. Нажмите "Добавить проект"
3. Введите название проекта (например, "Finanse MDK")
4. Отключите Google Analytics (или включите по желанию)
5. Нажмите "Создать проект"

#### 2.2. Добавление Android приложения

1. В Firebase Console выберите ваш проект
2. Нажмите на иконку Android
3. Введите:
   - **Package name**: `com.finanse.mdk`
   - **App nickname** (опционально): Finanse MDK
   - **Debug signing certificate SHA-1** (опционально, для тестирования)
4. Нажмите "Зарегистрировать приложение"
5. Скачайте файл `google-services.json`
6. Поместите файл в папку `app/` проекта (замените существующий)

#### 2.3. Настройка Authentication

1. В Firebase Console перейдите в раздел **Authentication**
2. Нажмите "Начать"
3. Включите провайдер **Email/Password**
4. Сохраните изменения

#### 2.4. Настройка Firestore

1. В Firebase Console перейдите в раздел **Firestore Database**
2. Нажмите "Создать базу данных"
3. Выберите режим:
   - **Режим тестирования** (для разработки)
   - **Режим продакшена** (для продакшена)
4. Выберите регион (например, `europe-west`)
5. Нажмите "Готово"

### 3. Настройка проекта в Android Studio

1. Откройте Android Studio
2. Выберите **File → Open** и выберите папку проекта
3. Дождитесь синхронизации Gradle (это может занять несколько минут)
4. Если появится запрос на обновление Gradle, согласитесь

### 4. Создание тестовых аккаунтов

#### Вариант 1: Через Firebase Console

1. Перейдите в **Authentication → Users**
2. Нажмите "Добавить пользователя"
3. Создайте пользователя:
   - Email: `user@test.com`
   - Password: (укажите пароль)
4. Повторите для администратора:
   - Email: `admin@test.com`
   - Password: (укажите пароль)

#### Вариант 2: Через приложение

1. Запустите приложение
2. Нажмите "Зарегистрироваться"
3. Создайте аккаунт с email `user@test.com` или `admin@test.com`
4. Приложение автоматически назначит роль ADMIN для `admin@test.com`

### 5. Сборка и запуск

#### Через Android Studio:

1. Подключите Android устройство или запустите эмулятор
2. Нажмите кнопку **Run** (▶️) или `Shift + F10`
3. Выберите устройство
4. Дождитесь установки и запуска приложения

#### Через командную строку:

```bash
# Сборка debug версии
./gradlew assembleDebug

# Установка на подключенное устройство
./gradlew installDebug

# Запуск через ADB
adb shell am start -n com.finanse.mdk/.MainActivity
```

### 6. Проверка работы

1. Запустите приложение
2. Зарегистрируйтесь или войдите с тестовым аккаунтом
3. Проверьте основные функции:
   - Добавление операции
   - Просмотр операций
   - Просмотр статистики
   - Управление категориями

## Решение проблем

### Ошибка: "google-services.json not found"

**Решение**: Убедитесь, что файл `google-services.json` находится в папке `app/` и содержит правильный package name.

### Ошибка: "Failed to resolve: firebase-*"

**Решение**: 
1. Проверьте подключение к интернету
2. Убедитесь, что в `build.gradle.kts` указан правильный plugin для Google Services
3. Выполните **File → Invalidate Caches / Restart** в Android Studio

### Ошибка: "Room cannot verify the data integrity"

**Решение**: Это может произойти при изменении схемы БД. Удалите приложение с устройства и переустановите.

### Ошибка компиляции SQLCipher

**Решение**: 
1. Убедитесь, что в `build.gradle.kts` добавлена зависимость `net.zetetic:android-database-sqlcipher`
2. Выполните **Build → Clean Project**, затем **Build → Rebuild Project**

### Приложение не запускается

**Решение**:
1. Проверьте логи в Logcat
2. Убедитесь, что минимальная версия SDK соответствует требованиям (API 26+)
3. Проверьте, что все зависимости загружены

## Дополнительная настройка

### Настройка пароля для шифрования БД

По умолчанию используется простой пароль. Для продакшена:

1. Откройте `FinanseDatabase.kt`
2. Замените `DB_PASSWORD` на безопасный пароль
3. Рекомендуется использовать Android Keystore для хранения пароля

### Настройка правил Firestore

Для продакшена настройте правила безопасности в Firebase Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Пользователи могут читать/писать только свои данные
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /transactions/{transactionId} {
      allow read, write: if request.auth != null && 
        resource.data.userId == request.auth.uid;
    }
    
    // Администраторы могут управлять системными категориями
    match /categories/{categoryId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
  }
}
```

## Готово!

Теперь приложение готово к использованию. Если возникнут вопросы, обратитесь к разделу "Решение проблем" или создайте issue в репозитории.





