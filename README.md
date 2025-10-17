# Carrier Vanity Name (Fork)

Forked version of the original Carrier Vanity Name app with additional functionality for changing SIM operator codes.

## What's New in This Fork

- **SIM Operator Code Modification**: Change numeric operator codes (MCC+MNC) using `setprop gsm.sim.operator.numeric`
- **Settings Persistence**: Automatically restore carrier names, ISO codes, and SIM operator codes after device reboot

## Features

### Original Functionality:
- Change carrier names on unrooted Android devices
- Override ISO country code of SIM cards

### Added:
- Set numeric operator codes for SIM1 and SIM2
- Automatic restoration of settings after reboot

## Requirements

- Android 8.0+ (API 26+)
- [Shizuku](https://shizuku.rikka.app/) installed and configured
- System-level permissions via Shizuku
- **Note:** May not work on all devices (without root)

## Original Project

Based on [CarrierVanityName](https://github.com/nullbytepl/CarrierVanityName) by nullbytepl.

---

# Carrier Vanity Name (Fork)

Форк оригинального приложения для изменения имен операторов связи на некорневых Android устройствах с дополнительным функционалом.

## Что добавлено в форке

- **Изменение кодов операторов SIM-карт** через команду `setprop gsm.sim.operator.numeric`
- **Сохранение настроек**: Автоматическое восстановление имен операторов, ISO кодов и кодов SIM-карт после перезагрузки

## Функционал

### Оригинал:
- Изменение отображаемого имени оператора связи
- Подмена ISO кода страны SIM-карты для обхода региональных ограничений

### Добавлено:
- Установка numeric кодов операторов (MCC+MNC) для SIM1 и SIM2
- Автоматическое восстановление настроек после перезагрузки устройства


## Технические требования

- Android 8.0+ (API 26+)
- Установленный и настроенный [Shizuku](https://shizuku.rikka.app/)
- **Примечание:** Может работать не на всех устройствах (без root)

## Оригинальный проект

Основан на [CarrierVanityName](https://github.com/nullbytepl/CarrierVanityName) от nullbytepl.
