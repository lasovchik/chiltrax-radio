# Chilltrax Radio

Нативный Android-клиент для интернет-радио Chilltrax. Минималистичный интерфейс,
фоновое воспроизведение и собственный список станций — без рекламы, аналитики и
лишних настроек.

Приложение создано для прослушивания Chilltrax, но поддерживает любые прямые
аудиопотоки: можно добавить собственные станции, изменить URL или удалить всё
лишнее.

## Возможности

- Воспроизведение потока Chilltrax в формате MP3.
- Станция Chilltrax с жанром Chillout создаётся при первом запуске.
- Добавление, редактирование и удаление радиостанций.
- Фоновое воспроизведение и управление из медиа-уведомления.
- Регулировка громкости, индикация буферизации и обработка ошибок.
- Проверка доступности потока и заполнение данных из каталога Radio Browser.
- Валидация названия и URL перед сохранением.
- Светлая и тёмная темы, Material 3, анимированная виниловая пластинка.
- Работа без сети с понятным сообщением об ошибке.

## Скачать

Готовый APK доступен на странице релизов:

**[Скачать последнюю версию](../../releases/latest)**

Требования: Android 8.0 (API 26) или новее.

APK подписан и готов к установке. SHA-256 и сведения о подписи указаны в описании
релиза.

## Технологии

| Компонент | Версия |
|---|---|
| Язык | Kotlin 2.4 |
| UI | Jetpack Compose, Material 3 |
| Архитектура | MVVM, Clean Architecture |
| Воспроизведение | Media3 ExoPlayer, MediaSessionService |
| Хранение | Room |
| DI | Hilt |
| Сеть | Retrofit, OkHttp, kotlinx.serialization |
| Минимальная версия | Android 8.0 (API 26) |
| Целевая версия | Android 17 (API 37) |

Проект использует корутины и Flow, а синхронная работа с БД и сетью выполняется
вне главного потока.

## Сборка

Понадобятся:

- Android Studio с поддержкой Android Gradle Plugin 9.4;
- JDK 17;
- Android SDK Platform 37 и Build Tools 37.0.0.

Клонируйте репозиторий и откройте папку проекта в Android Studio. При необходимости
укажите путь к SDK в `local.properties`:

```properties
sdk.dir=/path/to/android-sdk
```

Сборка из терминала:

```bash
./gradlew :app:assembleDebug
```

Готовый APK появится в `app/build/outputs/apk/debug/app-debug.apk`.

Установка на подключённое устройство:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Подписанный релиз

Параметры подписи передаются через переменные окружения и не хранятся в
репозитории:

```bash
export CHILLTRAX_KEYSTORE=/path/to/release.jks
export CHILLTRAX_KEY_ALIAS=chilltrax
export CHILLTRAX_KEYSTORE_PASSWORD='<store-password>'
export CHILLTRAX_KEY_PASSWORD='<key-password>'

./gradlew :app:assembleRelease
```

Подписанный APK: `app/build/outputs/apk/release/app-release.apk`.

Храните keystore и пароли в надёжном месте. Потеря ключа сделает невозможным
выпуск обновлений с той же подписью.

## Структура проекта

```text
app/src/main/java/com/chilltrax/radio/
├── data/            # Room, Retrofit, реализации репозиториев
├── di/              # модули Hilt
├── domain/          # модели, use cases, валидация
├── playback/        # ExoPlayer, MediaSessionService, MediaController
└── presentation/    # Compose-экраны, ViewModel, навигация, тема
```

Схема базы данных Room экспортируется в `app/schemas/`.

## Проверки

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

Юнит-тесты покрывают валидацию пользовательского ввода. Lint настроен на сборку
без ошибок; предупреждения о доступных обновлениях зависимостей не блокируют
релиз.

## Примечания

- Поддерживаются как HTTPS-, так и HTTP-потоки: часть радиостанций до сих пор
  использует незашифрованный HTTP.
- На Android 13+ для показа уведомления требуется разрешение на уведомления.
  Воспроизведение работает и без него.
- Приложение не собирает аналитику и не передаёт данные третьим лицам. Сетевые
  запросы используются только для загрузки потока и поиска станций в Radio
  Browser.
