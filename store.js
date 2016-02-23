import numberCounterApp from './reducers'
import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'

export default createStore(
    numberCounterApp,
    applyMiddleware(thunk)
);