/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';
import React, {
    AppRegistry,
    Component,
    StyleSheet,
    Text,
    TouchableNativeFeedback,
    View
} from 'react-native';

import { toggleState } from './actions'
import { connect, Provider } from 'react-redux'
import ServiceTopLevel from './ServiceTopLevel';
import store from './store'


class NumberCounter extends Component {
    render() {
        return <Provider store={store}>
            <NumberCounterContainer />
        </Provider>
    }
}

/**
 * Presentational component.
 */
const NumberCounterUI = ({isPlaying, value, onPress}) => (
    <View style={styles.container}>
        <Text style={styles.welcome} onPress={onPress}>
            {isPlaying ? "Pause" : "Play"}
        </Text>

        <Text style={styles.instructions}>
            {value}
        </Text>
    </View>
);

const mapStateToProps = (state) => {
    return {
        isPlaying: state.isPlaying,
        value: state.value,
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        onPress: () => {
            dispatch(toggleState());
        }
    };
};


const NumberCounterContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(NumberCounterUI);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});

AppRegistry.registerComponent('NumberCounter', () => NumberCounter);
