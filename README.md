# NUNUX Droid

Nunux Droid is a XMPP bot for Android device.
It allow you to control your Android phone via XMPP commands.

## Prerequisites

In order to chat with your phone, you need to create an XMPP account.
Here a list of [free XMPP server](https://list.jabber.at/)

## Features

* Print availables commands.

    help

* Make the phone ring indefinitely (until "alarm stop" command).

    alarm start
    alarm stop

* Copy text into clipboard

    copy a big big text that would be painful to type with virtual keyboard

* Give device location

    location

* Use TTS engine to say what you want

    tell my god it''s full of stars

* Open an URL into the device browser

    http://www.nunux.org

* Send an SMS to a phone number

    sms 123456789 what's up?

* Give the device IP address

    ip

* Give the device call log (incoming, outgoing, missed)

    log out
    log in
    log missed

* Give the device SMS log (incoming, outgoing)

    sms in
    sms out

* Make a call

    call 123456789

## Build

This project is build with Maven.

    mvn package

An APK wil be created in the target directory.

A builded version can be found [here](//www.nunux.org/droid.apk).

----------------------------------------------------------------------

NUNUX Droid

Copyright (c) 2011 Nicolas CARLIER (https://github.com/ncarlier)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

----------------------------------------------------------------------
