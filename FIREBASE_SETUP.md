# Пошаговая инструкция по настройке Firebase

## Шаг 1: Создание проекта в Firebase Console

### 1.1. Вход в Firebase Console

1. Откройте браузер и перейдите на [https://console.firebase.google.com/](https://console.firebase.google.com/)
2. Войдите в свой Google аккаунт (если не авторизованы)

### 1.2. Создание нового проекта

1. На главной странице Firebase Console нажмите кнопку **"Добавить проект"** (или "Create a project")
2. **Шаг 1: Название проекта**
   - Введите название проекта: `Finanse MDK` (или любое другое)
   - Нажмите **"Продолжить"** (Continue)

3. **Шаг 2: Google Analytics** (опционально)
   - Рекомендуется **отключить** Google Analytics для тестирования
   - Или оставить включенным, если нужна аналитика
   - Нажмите **"Продолжить"**

4. **Шаг 3: Настройка Analytics** (если включен)
   - Выберите или создайте аккаунт Google Analytics
   - Нажмите **"Создать проект"** (Create project)

5. Дождитесь создания проекта (обычно 30-60 секунд)
6. Нажмите **"Продолжить"** когда проект будет готов

---

## Шаг 2: Добавление Android приложения

### 2.1. Добавление Android приложения в проект

1. В Firebase Console выберите ваш проект (если не выбран)
2. На главной странице проекта найдите иконку **Android** (или нажмите на шестеренку ⚙️ → "Настройки проекта")
3. В разделе "Ваши приложения" нажмите **"Добавить приложение"** → выберите **Android**

### 2.2. Регистрация приложения

Заполните форму:

1. **Имя пакета Android (обязательно)**
   ```
   com.finanse.mdk
   ```
   ⚠️ **ВАЖНО**: Это должно точно совпадать с `applicationId` в `app/build.gradle.kts`

2. **Псевдоним приложения** (необязательно)
   ```
   Finanse MDK
   ```

3. **Сертификат для подписи отладочного приложения** (SHA-1) (необязательно)
   - Для тестирования можно пропустить
   - Для продакшена рекомендуется добавить

4. Нажмите **"Зарегистрировать приложение"**

### 2.3. Скачивание google-services.json

1. После регистрации откроется страница с инструкциями
2. Нажмите кнопку **"Скачать google-services.json"**
3. Сохраните файл в удобное место (например, на Рабочий стол)

### 2.4. Установка google-services.json в проект

1. Откройте папку вашего проекта: `finanse-mdk/`
2. Перейдите в папку `app/`
3. **Скопируйте** скачанный файл `google-services.json` в папку `app/`
4. **Замените** существующий файл (если он есть)

   Структура должна быть такой:
   ```
   finanse-mdk/
   └── app/
       ├── google-services.json  ← ВОТ ЗДЕСЬ
       ├── build.gradle.kts
       └── ...
   ```

5. Убедитесь, что файл называется именно `google-services.json` (без дополнительных символов)

### 2.5. Проверка google-services.json

Откройте файл `app/google-services.json` и проверьте, что он содержит:

```json
{
  "project_info": {
    "project_number": "...",
    "project_id": "ваш-проект-id",
    "storage_bucket": "..."
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "...",
        "android_client_info": {
          "package_name": "com.finanse.mdk"  ← Должно совпадать
        }
      },
      ...
    }
  ]
}
```

---

## Шаг 3: Настройка Authentication (Аутентификация)

### 3.1. Включение Email/Password провайдера

1. В Firebase Console выберите ваш проект
2. В левом меню найдите раздел **"Authentication"** (или "Аутентификация")
3. Нажмите на него
4. Если видите сообщение "Get started", нажмите **"Начать"** (Get started)

### 3.2. Настройка методов входа

1. Перейдите на вкладку **"Способы входа"** (Sign-in method)
2. Найдите в списке **"Пароль"** (Email/Password) или **"Электронная почта"**
3. Нажмите на него
4. Переключите переключатель в положение **"Включено"** (Enabled)
5. Нажмите **"Сохранить"** (Save)

### 3.3. (Опционально) Настройка доменов

- По умолчанию Firebase разрешает регистрацию с любого email
- Для продакшена можно настроить авторизованные домены

---

## Шаг 4: Настройка Firestore Database

### 4.1. Создание базы данных

1. В Firebase Console выберите ваш проект
2. В левом меню найдите раздел **"Firestore Database"** (или "База данных Firestore")
3. Нажмите на него
4. Нажмите кнопку **"Создать базу данных"** (Create database)

### 4.2. Выбор режима безопасности

Выберите один из вариантов:

**Вариант А: Режим тестирования** (для разработки)
- ✅ Выберите **"Начать в режиме тестирования"** (Start in test mode)
- ⚠️ **Внимание**: Этот режим открывает доступ к данным на 30 дней
- Подходит для разработки и тестирования

**Вариант Б: Режим продакшена** (рекомендуется)
- ✅ Выберите **"Начать в безопасном режиме"** (Start in production mode)
- Требует настройки правил безопасности
- Более безопасный вариант

### 4.3. Выбор местоположения

1. Выберите регион для хранения данных
   - Рекомендуется: `europe-west` (для России/Европы)
   - Или `us-central` (для США)
2. Нажмите **"Включить"** (Enable)

### 4.4. Ожидание создания базы данных

- Создание базы данных займет 1-2 минуты
- Дождитесь сообщения об успешном создании

### 4.5. Настройка правил безопасности (для режима тестирования)

Если выбрали режим тестирования, правила будут такими:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2024, 12, 31);
    }
  }
}
```

⚠️ **ВАЖНО**: Эти правила временные (30 дней). Для продакшена используйте правила ниже.

### 4.6. Настройка правил безопасности (для продакшена)

1. Перейдите на вкладку **"Правила"** (Rules) в Firestore
2. Замените правила на следующие:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Пользователи могут читать/писать только свои данные
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Транзакции - только свои
    match /transactions/{transactionId} {
      allow read, write: if request.auth != null && 
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && 
        request.resource.data.userId == request.auth.uid;
    }
    
    // Категории - все могут читать, админы могут писать
    match /categories/{categoryId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
    
    // Бюджеты - только свои
    match /budgets/{budgetId} {
      allow read, write: if request.auth != null && 
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && 
        request.resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Нажмите **"Опубликовать"** (Publish)

---

## Шаг 5: Проверка настройки

### 5.1. Проверка в Android Studio

1. Откройте проект в Android Studio
2. Убедитесь, что файл `google-services.json` находится в папке `app/`
3. Синхронизируйте проект: **File → Sync Project with Gradle Files**
4. Проверьте, что нет ошибок в консоли

### 5.2. Проверка зависимостей

Убедитесь, что в `app/build.gradle.kts` есть:

```kotlin
plugins {
    ...
    id("com.google.gms.google-services")
}

dependencies {
    ...
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}
```

### 5.3. Тестовый запуск

1. Подключите Android устройство или запустите эмулятор
2. Запустите приложение
3. Попробуйте зарегистрироваться с новым email
4. Проверьте в Firebase Console → Authentication → Users, что пользователь появился

---

## Решение проблем

### Проблема: "google-services.json not found"

**Решение:**
- Убедитесь, что файл находится именно в папке `app/`
- Проверьте название файла (должно быть `google-services.json`)
- Выполните **File → Invalidate Caches / Restart** в Android Studio

### Проблема: "Package name mismatch"

**Решение:**
- Проверьте, что в `app/build.gradle.kts` указано: `applicationId = "com.finanse.mdk"`
- Проверьте, что в `google-services.json` указан тот же package name
- Пересоздайте `google-services.json` в Firebase Console

### Проблема: "FirebaseApp not initialized"

**Решение:**
- Убедитесь, что плагин `com.google.gms.google-services` добавлен в `app/build.gradle.kts`
- Убедитесь, что файл `google-services.json` в правильном месте
- Выполните **Build → Clean Project**, затем **Build → Rebuild Project**

### Проблема: "Authentication failed"

**Решение:**
- Проверьте, что Email/Password провайдер включен в Firebase Console
- Проверьте правила Firestore (должны разрешать запись)
- Проверьте интернет-соединение

---

## Дополнительные настройки (опционально)

### Настройка индексов Firestore

Если приложение будет использовать сложные запросы, может потребоваться создать индексы:

1. Перейдите в Firestore → **"Индексы"** (Indexes)
2. Firebase автоматически предложит создать индексы при необходимости
3. Нажмите на ссылку в ошибке и создайте индекс

### Настройка квот

1. Перейдите в **Настройки проекта** → **Использование и выставление счетов**
2. Проверьте текущие лимиты
3. Для тестирования бесплатного плана достаточно

---

## Готово! ✅

После выполнения всех шагов Firebase настроен и готов к использованию. Приложение сможет:
- Регистрировать и авторизовывать пользователей
- Сохранять данные в Firestore
- Синхронизировать данные между устройствами

Если возникнут вопросы, обратитесь к разделу "Решение проблем" или документации Firebase.





