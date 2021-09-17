import config from '../../../share/common/util/config.js'
import Log from '../../common/util/log.js'
const JS_TAG = "JS/Component/Device info: ";

export default {
  props:['isFullScreen', 'productId'],
  computed:{
    deviceImgStyle(){
      return config.deviceImgStyle;
    },
    deviceResource(){
      Log.info("productId = " + this.productId)
      return this.$t('resources.' + this.productId);
    }
  },
  onInit() {
    console.info(JS_TAG + "onInit");
  }
}