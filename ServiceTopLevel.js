import BatchedBridge from 'BatchedBridge';

import { pause } from './actions'
import store from './store'

const ServiceTopLevel = {
    runService: (foo) => {
        console.log("Hit the service top level code!");
    },
    pause: (foo) => {
        console.log("pausing!!!");
        store.dispatch(pause());
    }
};

BatchedBridge.registerCallableModule(
    'ServiceTopLevel',
    ServiceTopLevel
);

export default ServiceTopLevel;