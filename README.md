# YcoordCore

Core-библиотека на Java для проектов экосистемы **Ycoord**: общие утилиты, базовые абстракции и инфраструктурный код, который переиспользуется в других плагинах.

> Репозиторий написан на Java и собирается Maven’ом (`pom.xml` в корне).

---

## Возможности

- Различные типы баланса, легко вызываемые из кода
- Командный процессор. Примеры [YcoordExamples](https://github.com/ycoord/YcoordExamples)
- Обширный асинхронный GUI. Примеры [YcoordExamples](https://github.com/ycoord/YcoordExamples)
- Обширная система асинхронных сообщений. Примеры [YcoordExamples](https://github.com/ycoord/YcoordExamples)
- Предустановленные частицы **WIP**
- Для каждого плагина, наследуемого от *YcoordPlugin* добавляется доступ к хранилищу КЛЮЧ-ЗНАЧЕНИЕ для игрока. Варианты: MySql, Postgresql, Sqlite
- Обширное расширение плейсхолдеров, добавляюее градиент и внутренние плейсхолдеры
- Остальные всмопогатльные утилиты, для плагинов экосистемы

---


## Зависимости

- [ОПЦИОНАЛЬНО] [PlayerPoints](https://www.spigotmc.org/resources/playerpoints.80745/)
- [ОПЦИОНАЛЬНО] [Vault](https://www.spigotmc.org/resources/vault.34315/)
- [ОБЯЗАТЕЛЬНО] [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [ОБЯЗАТЕЛЬНО] [NBTApi](https://www.spigotmc.org/resources/nbt-api.7939/)

## Требования

- JDK **17+** (или версия, указанная в `pom.xml`)
- Maven **3.8+**
