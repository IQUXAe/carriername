# Carrier Vanity Name (Fork)

Forked version of the original Carrier Vanity Name app with extended functionality for carrier configuration and operator switching.

## Features

### Carrier Switcher (Full Operator Override)
- Complete carrier operator switching using TelephonyFrameworkInitializer
- 80+ preset carriers grouped by country
- Custom carrier and country code input
- Bypasses regional restrictions and carrier limitations
- Affects system behavior, not just display name

### Original Functionality
- Change carrier display name in status bar
- Override ISO country code of SIM cards
- Set numeric operator codes (MCC+MNC) via `setprop gsm.sim.operator.numeric`

### Settings Persistence
- Automatic restoration of all settings after device reboot
- Separate persistence for carrier switcher and display name settings

## Requirements

- Android 8.0+ (API 26+)
- [Shizuku](https://shizuku.rikka.app/) installed and running
- System-level permissions via Shizuku

## Warnings

- Carrier switching may cause unexpected behavior with network-dependent apps
- Some devices may reject configuration overrides

## Original Project

Based on [CarrierVanityName](https://github.com/nullbytepl/CarrierVanityName) by nullbytepl.

---

# Carrier Vanity Name (Fork)

Форк оригинального приложения с расширенным функционалом для настройки оператора связи.

## Функционал

### Carrier Switcher (Полная смена оператора)
- Полная подмена конфигурации оператора через TelephonyFrameworkInitializer
- 80+ предустановленных операторов с группировкой по странам
- Ввод произвольного оператора и кода страны
- Обход региональных блокировок и ограничений оператора
- Влияет на поведение системы, а не только на отображение

### Оригинальный функционал
- Изменение отображаемого имени оператора в статус-баре
- Подмена ISO кода страны SIM-карты
- Установка numeric кодов операторов (MCC+MNC) через `setprop gsm.sim.operator.numeric`

### Сохранение настроек
- Автоматическое восстановление всех настроек после перезагрузки
- Раздельное сохранение для carrier switcher и отображаемого имени

## Технические требования

- Android 8.0+ (API 26+)
- Установленный и запущенный [Shizuku](https://shizuku.rikka.app/)
- Системные разрешения через Shizuku

## Предупреждения

- Смена оператора может вызвать неожиданное поведение сетевых приложений
- Некоторые устройства могут отклонять переопределение конфигурации

## Оригинальный проект

Основан на [CarrierVanityName](https://github.com/nullbytepl/CarrierVanityName) от nullbytepl.
