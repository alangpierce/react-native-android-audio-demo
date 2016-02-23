import BatchedBridge from 'BatchedBridge';

import { pause, play } from './actions'
import store from './store'

const ServiceTopLevel = {
    runService: (foo) => {
        console.log("Hit the service top level code!");
    },
    pause: (foo) => {
        store.dispatch(pause());
    },
    play: (foo) => {
        store.dispatch(play());
    }
};

BatchedBridge.registerCallableModule(
    'ServiceTopLevel',
    ServiceTopLevel
);

export default ServiceTopLevel;