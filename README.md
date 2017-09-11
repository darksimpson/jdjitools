# jdjitools
Java DJI Tools

![pic](https://raw.githubusercontent.com/darksimpson/jdjitools/master/jdjitools.jpg)

A collection of various tools/snippets tied in one CLI shell-like application.
Completely written in Java, so pretend to be all-in-one combine cross-platform and multi-OS solution.
All tools will be implemented as clean and convenient separate classes with small amount of dependencies so you can extend or reuse it as you want (preserving copyright of course). This means also that you can write your own tools to extend this project and make pull requests.
Despite that almost all of the functionality is available in other #DeejayeyeHackingClub projects, may be someone will find this project useful.
Also you need to know that I have a small spare time to work on this project so overall progress can move veeeery slowly.

At this moment available:
- Key derivation util: A clone of internal DJI algo that may be helpful if you dig deeply in reversing
- FTPd decryptor util: Decrypt files downloaded from DJI product onboard FTP server (with correct descrambling of first bytes and padding handling)
- DUML monitor util: Monitor and display (or save to text file) incoming DUML traffic from serial device
- DUML decoder util: Decode and display (or save to text file) DUML traffic from (captured) binary file
- DUML command util: Possibility to freely execute any needed DUML command

WIP now (need some rework/clean up before publishing):
- FC params util: Read/write UAV FC params using DUML to f.e. unlock some product restrictions (Assistant-less)

Plans:
- Rooting util: Port of P0VsRedHerring exploit using DUML (Assistant-less), may be some others exploits in future
- Updater util: Send FW update package to DJI product and monitor progress (Assistant-less)
- Updates downloader: Download FW update packages from DJI servers and make offline package suitable to use with Updater util
- Firmware manipulation util: Completely unpack and pack back FW images (may be useful for reversing)
- ... other useful stuff (you can suggest it) ...

### #DeejayeyeHackingClub information repos aka "The OG's" (Original Gangsters)

http://dji.retroroms.info/ - "Wiki"

https://github.com/fvantienen/dji_rev - This repository contains tools for reverse engineering DJI product firmware images.

https://github.com/Bin4ry/deejayeye-modder - APK "tweaks" for settings & "mods" for additional / altered functionality

https://github.com/hdnes/pyduml - Assistant-less firmware pushes and DUMLHacks referred to as DUMBHerring when used with "fireworks.tar" from RedHerring. DJI silently changes Assistant? great... we will just stop using it.

https://github.com/MAVProxyUser/P0VsRedHerring - RedHerring, aka "July 4th Independence Day exploit", "FTPD directory transversal 0day", etc. (Requires Assistant). We all needed a public root exploit... why not burn some 0day?

https://github.com/MAVProxyUser/dji_system.bin - Current Archive of dji_system.bin files that compose firmware updates referenced by MD5 sum. These can be used to upgrade and downgrade, and root your I2, P4, Mavic, Spark, Goggles, and Mavic RC to your hearts content. (Use with pyduml or DUMLDore)

https://github.com/MAVProxyUser/firm_cache - Extracted contents of dji_system.bin, in the future will be used to mix and match pieces of firmware for custom upgrade files. This repo was previously private... it is now open.

https://github.com/MAVProxyUser/DUMLrub - Ruby port of PyDUML, and firmware cherry picking tool. Allows rolling of custom firmware images.

https://github.com/jezzab/DUMLdore - Even windows users need some love, so DUMLDore was created to help archive, and flash dji_system.bin files on windows platforms.

https://github.com/MAVProxyUser/DJI_ftpd_aes_unscramble - DJI has modified the GPL Busybox ftpd on Mavic, Spark, & Inspire 2 to include AES scrambling of downloaded files... this tool will reverse the scrambling

https://github.com/darksimpson/jdjitools - Java DJI Tools, a collection of various tools/snippets tied in one CLI shell-like application.
