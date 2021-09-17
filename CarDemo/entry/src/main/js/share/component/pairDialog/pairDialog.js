import app from '@system.app';
import state from '../../common/util/state.js'
import Log from '../../common/util/log.js'

const showState = [state.pair.PAIR]
const JS_TAG = "JS/Component/Pair dialog: ";

export default {
    props: ['state', 'productId'],
    computed: {
        showObj() {
            return showState.indexOf(this.state) > -1;
        },
        deviceResource() {
            // 没有产品Id 先将字段写死
            Log.info('productId:' + this.productId)
            return this.$t('resources.' + this.productId);
//            return this.$t('resources')
        }
    },
    onInit() {
        console.info(JS_TAG + 'onInit');
    },
    exit() {
        app.terminate();
    },
}