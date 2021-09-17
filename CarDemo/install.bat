hdc shell bm uninstall com.qihoo.seat.fa
hdc shell rm -rf /sdcard/caomenglin
hdc shell mkdir -p /sdcard/caomenglin
hdc file send build\outputs\hap\debug\phone\entry-debug-rich-signed.hap /sdcard/caomenglin/
hdc shell bm install -r /sdcard/caomenglin

@pause
