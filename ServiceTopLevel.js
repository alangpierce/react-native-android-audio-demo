import BatchedBridge from 'BatchedBridge';

const ServiceTopLevel = {
    runService: (foo) => {
        console.log("Hit the service top level code!");
    }
};

BatchedBridge.registerCallableModule(
    'ServiceTopLevel',
    ServiceTopLevel
);

export default ServiceTopLevel;