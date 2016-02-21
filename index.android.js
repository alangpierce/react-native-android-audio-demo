/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';
import React, {
    AppRegistry,
    Component,
    NativeModules,
    StyleSheet,
    Text,
    TouchableNativeFeeqdback,
    View
} from 'react-native';

import tts from 'react-native-android-speech'
import ServiceTopLevel from './ServiceTopLevel';

/**
 * Container component.
 */
class NumberCounter extends Component {
    constructor() {
        super();
        this.state = {
            value: 0,
            isPlaying: false,
            intervalId: null,
        }
    }

    render() {
        const {isPlaying, value} = this.state;
        return <NumberCounterUI
            isPlaying={isPlaying}
            value={value}
            onPress={this.toggleState.bind(this)}
        />;
    }

    toggleState() {
        if (this.state.isPlaying) {
            this.pause();
        } else {
            this.play();
        }
    }

    pause() {
        clearInterval(this.state.intervalId);
        this.setState({
            isPlaying: false,
            intervalId: null,
        });
    }

    play() {
        const intervalId = setInterval(
            () => this.countUp(),
            1000);
        this.setState({
            isPlaying: true,
            intervalId: intervalId,
        })
    }

    countUp() {
        const newValue = this.state.value + 1;
        tts.speak({
            text: '' + newValue,
        });

        this.setState({
            value: newValue,
        });
    }
}

/**
 * Presentational component.
 */
class NumberCounterUI extends Component {
    render() {
        const {isPlaying, value, onPress} = this.props;
        return (
            <View style={styles.container}>
                <Text style={styles.welcome} onPress={onPress}>
                    {isPlaying ? "Pause" : "Play"}
                </Text>

                <Text style={styles.instructions}>
                    {value}
                </Text>
            </View>
        );
    }
}

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

console.log("Hello from JS land!");

AppRegistry.registerComponent('NumberCounter', () => NumberCounter);