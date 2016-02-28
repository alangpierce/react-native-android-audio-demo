import React from 'react-native'
import BatchedBridge from 'BatchedBridge';

// TODO: Investigate if we could re-use the callback mechanism already provided
// by React Native.
const CALLBACKS_BY_ID = {};


export const processCallbacks = (view) => {
    const newProps = {
        // As a hack, just give any key to the elements so we don't get a React
        // warning.
        key: Math.random().toString(36).slice(2),
    };
    if (view.props.hasOwnProperty("onPress")) {
        newProps.onPress = generateIdForFunction(view.props.onPress);
        console.log("Set callback ID to " + view.props.onPress);
    }

    const newChildren = [];
    if (view.props.hasOwnProperty("children")) {
        view.props.children.forEach((child) => {
            newChildren.push(processCallbacks(child));
        });
    }
    return React.cloneElement(view, newProps, newChildren);
};


/**
 * Generate an ID for this function and track it so that we can look it up
 * later.
 */
const generateIdForFunction = (func) => {
    for (const callbackId in CALLBACKS_BY_ID) {
        // TODO: Yuck, this is a linear scan. Ideally we would keep a hash
        // table.
        if (CALLBACKS_BY_ID.hasOwnProperty(callbackId) &&
            CALLBACKS_BY_ID[callbackId] === func) {
            return callbackId;
        }
    }
    var newCallbackId = Math.random().toString(36).slice(2);
    CALLBACKS_BY_ID[newCallbackId] = func;
    return newCallbackId;
};


const RemoteViewsModule = {
    runCallback: (callbackId) => {
        console.log("Invoking callback " + callbackId);
        CALLBACKS_BY_ID[callbackId]();
    },
};

BatchedBridge.registerCallableModule(
    'RemoteViewsModule',
    RemoteViewsModule
);


export const LinearLayout = "LinearLayout";
export const TextView = "TextView";
export const ImageButton = "ImageButton";
