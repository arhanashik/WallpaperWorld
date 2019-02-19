## Firebase Analytics DebugView

To enable Analytics Debug mode on an emulated Android device, execute the following command line:
`adb shell setprop debug.firebase.analytics.app <package_name>`

This behavior persists until you explicitly disable Debug mode by executing the following command line:
`adb shell setprop debug.firebase.analytics.app .none.`