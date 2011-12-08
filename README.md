SwUDi - Swing USB Display
=========================

SwUDi is a small framework to use java and swing for controlling an external usb device.

SwUDi uses [JGoodies Form Layout](http://www.jgoodies.com/freeware/forms/index.html), I think version 1.2.1 should be available in maven central.

SwUDi uses a JWindow as peer to get the repainting done. However, popups and optionpane's did not work, as they create their own peer.
Repainting is done only if a window is dirty, SwUDi therefore installs its own RepaintManager, which may clash with other RepaintManagers like swingx ExtensibleRepaintManager.