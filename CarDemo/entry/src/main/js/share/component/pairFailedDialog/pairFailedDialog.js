import app from '@system.app';
import state from '../../common/util/state.js'
import Log from '../../common/util/log.js'

const showState = [state.pair.PAIR_FAILED, state.pair.SCOPE_DENIED, state.pair.REPAIR_REQUESTED,
    state.pair.PERMISSION_DENIED, state.pair.REPAIR_FAILED]

export default {
    props: ["state", 'productId'],
    computed: {
        showObj() {
            return showState.indexOf(this.state) > -1;
        },
        deviceResource() {
            Log.info("pairfial productId:" + this.productId)
            return this.$t('resources.' + this.productId);
        },
    },
    onInit() {
        Log.info("pair failed onInit");

        this.$watch('state', 'onStateChange');
    },
    onStateChange() {
        Log.info("onStateChange state = " + this.state);
    },
    exit() {
        Log.info("app terminate5")
        app.terminate();
    },
    reConnect() {
        Log.info("reConnect")
        this.$emit('onReConnect');
    }
}
