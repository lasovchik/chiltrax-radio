# Chilltrax Radio

Нативный Android-клиент интернет-радио Chilltrax: Compose UI, Media3 ExoPlayer,
фоновое воспроизведение, медиа-уведомление, управление станциями в Room.

## Возможности

- Воспроизведение потока `https://streamssl.chilltrax.com/stream.mp3`.
- Станция `Chilltrax` (жанр `Chillout`) создаётся при первом запуске.
- Плейлист станций: добавление, редактирование, удаление, выбор активной.
- Фоновое воспроизведение через `MediaSessionService`, уведомление с play/pause.
- Регулировка громкости, статус "буферизация", обработка ошибок и отсутствия сети.
- Поиск названия/жанра станции по URL через Radio Browser API (не блокирует сохранение).
- Валидация URL и полей формы.
- Тёмная/светлая тема, Material 3, анимированная виниловая пластинка и эквалайзер.

## Технологии

| Компонент | Версия |
|---|---|
| Kotlin / KGP | 2.4.10 |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.8.2 |
| Hilt | 2.60.1 |
| KSP | 2.3.12 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 26 |
| Jetpack Compose BOM | 2026.09.00 |
| Media3 | 1.11.1 |
| Room | 2.8.5 |
| Retrofit / OkHttp | 2.12.0 / 4.12.0 |

`compileSdk 37` и AGP 9.4.1 выбраны потому, что используемые версии AndroidX и
Hilt 2.60.1 требуют AGP 9.1+. В `gradle.properties` отключены встроенный Kotlin
AGP и новый DSL (`android.builtInKotlin=false`, `android.newDsl=false`), так как
Hilt 2.60.1 ещё не совместим со встроенным Kotlin AGP 9.4.1 в связке с KGP 2.4.10.

## Структура

```text
app/src/main/java/com/chilltrax/radio/
├── ChilltraxApp.kt                  # @HiltAndroidApp
├── MainActivity.kt                  # edge-to-edge, запрос POST_NOTIFICATIONS
├── data/
│   ├── local/                       # Room: entity, DAO, database, default station
│   ├── remote/                      # Retrofit: StreamApi, RadioBrowserApi, DTO
│   └── repository/                  # StationRepositoryImpl, StreamRepositoryImpl, NetworkMonitor
├── di/                              # Hilt: Database, Network, Repository modules
├── domain/
│   ├── model/                       # Station, StationSuggestion
│   ├── repository/                  # интерфейсы репозиториев
│   ├── usecase/                     # StationUseCases, NetworkUseCases
│   └── validation/                  # StationInputValidator
├── playback/
│   ├── PlaybackService.kt           # MediaSessionService + ExoPlayer
│   ├── RadioPlayer.kt               # singleton MediaController
│   └── ArtworkFactory.kt            # обложка по умолчанию
└── presentation/
    ├── common/                      # UiMessage
    ├── editor/                      # StationEditorScreen + ViewModel
    ├── home/                        # PlayerScreen, RadioViewModel, VinylRecord, AudioVisualizer
    ├── navigation/                  # ChilltraxNavHost
    └── theme/                       # Color, Theme, Type
```

Схема Room экспортируется в `app/schemas/`.

## Сборка на удалённом сервере

Все команды выполняются через `bifrost_exec` (не через `ssh`).

```bash
# один раз за сессию
bifrost_connect
```

На сервере выполняются команды:

```bash
cd /root/chilltrax-radio
printf 'sdk.dir=/opt/android-sdk\n' > local.properties
./gradlew :app:assembleDebug --no-daemon
./gradlew :app:testDebugUnitTest --no-daemon
./gradlew :app:lintDebug --no-daemon
./gradlew :app:assembleRelease --no-daemon
```

Каждая команда запускается через `bifrost_exec`, например:

```text
bifrost_exec({ command: "cd /root/chilltrax-radio && ./gradlew :app:assembleDebug --no-daemon" })
```

Артефакты:

- debug APK: `/root/chilltrax-radio/app/build/outputs/apk/debug/app-debug.apk`
- unsigned release APK: `/root/chilltrax-radio/app/build/outputs/apk/release/app-release-unsigned.apk`

Скачать APK на локальную машину:

```text
bifrost_download(remotePath="/root/chilltrax-radio/app/build/outputs/apk/debug/app-debug.apk",
                 localPath="/home/opencode/workspace/app-debug.apk")
```

Установка на подключённый по ADB телефон:

```bash
adb install -r app-debug.apk
```

Сервер собирает проект без эмулятора: в нём нет system images и `/dev/kvm`,
поэтому запуск на устройстве/эмуляторе нужно проверить отдельно.

## Локальная сборка

1. Открыть папку проекта в Android Studio, поддерживающей AGP 9.4.1.
2. Указать JDK 17 и Android SDK с platform 37 и build-tools 37.0.0.
3. Создать `local.properties`:

```properties
sdk.dir=/path/to/android-sdk
```

4. `./gradlew :app:assembleDebug` или Run в Android Studio.
5. Для release задать signingConfig в `app/build.gradle.kts`.

## Проверки

- `StationInputValidatorTest`: 5 тестов, все проходят.
- `lintDebug`: 0 ошибок. Остались только предупреждения о доступных более новых
  версиях Retrofit/OkHttp/Coroutines и о `mipmap-anydpi-v26` при `minSdk 26`
  (папка нужна AAPT для adaptive icon).
- Debug и release (R8) сборки успешно собираются.

## Git и Gitea

Gitea-репозиторий должен существовать до `git push`. Создать его можно в веб-UI,
через `tea` или API. Доступ к Git — по SSH.

```bash
cd /home/opencode/workspace/chilltrax-radio

git init
git add .
git commit -m "feat: Chilltrax Radio Android client"
git branch -M main

# заменить gitea.example.com и USER на реальные значения
git remote add origin git@gitea.example.com:USER/chilltrax-radio.git
git push -u origin main
```

Проверка SSH-доступа:

```bash
ssh -T git@gitea.example.com
```

Создание репозитория через `tea` (если установлен и настроен):

```bash
tea repos create --name chilltrax-radio --private \
  --description "Native Android client for Chilltrax radio"
```

Создание через API Gitea:

```bash
export GITEA_URL="https://gitea.example.com"
export GITEA_TOKEN="<personal-access-token>"

curl -X POST \
  -H "Authorization: token $GITEA_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"chilltrax-radio","private":true,"description":"Native Android client for Chilltrax radio"}' \
  "$GITEA_URL/api/v1/user/repos"
```

Если репозиторий уже создан, команду создания пропустить.

## Особенности

- `usesCleartextTraffic="true"` включён, чтобы можно было добавлять HTTP-потоки:
  многие интернет-радиостанции до сих пор отдают `http://`.
- На Android 13+ уведомление воспроизведения появляется после разрешения
  `POST_NOTIFICATIONS`; воспроизведение работает и без него.
- Room-схема версии 1 лежит в `app/schemas/`.
