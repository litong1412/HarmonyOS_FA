import app from '@system.app';
import router from '@system.router'
import Log from '../../common/util/log.js'
const JS_TAG = "JS/Component/Title bar: ";

export default {
    props:['productId', 'showObj', 'menuOptions', 'title'],
    computed:{
        showMenu(){
            Log.info("length = " + (this.menuOptions && this.menuOptions.length >0))
            return this.menuOptions && this.menuOptions.length >0? true : false;
        },
        displayedTitle(){
            if (this.title == undefined) {
                Log.info("name = " + this.$t('resources.' + this.productId))
                Log.info("name = " + this.$t('resources.' + this.productId).device_name)
                return this.$t('resources.' + this.productId).device_name;
            } else {
                return this.title;
            }
        }
    },
    onInit() {
        console.info(JS_TAG + "onInit");
    },
    onMenuSelected: function (e) {
        try{
            console.debug(JS_TAG + 'onMenuSelected resources  = ' + JSON.stringify(this.menuOptions));
            let option = this.menuOptions[e.value];
            console.debug(JS_TAG + 'onMenuSelected option = ' + JSON.stringify(option));
            router.push({
                uri: option.uri,
                params: {
                    productId: this.productId,
                    title: option.label
                }
            });
            this.$emit('onLeavePage');
        }catch(error){
            console.error(JS_TAG + 'onMenuSelected error = ' + error);
        }
    },
    returnBack() {
        Log.info("returnBack");


        this.$emit("backClicked");
    },
}