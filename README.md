# React Native Android Audio Demo

This is a proof of concept for how to build an audio app (like a music of
podcast player) for Android in React Native, as of early 2016. Unlike previous
examples, this app properly creates an Android `Service` to manage audio, which
means that the audio will keep playing even when the `Activity` is destroyed.
Using a `Service` required opening some escape hatches in React Native, since it
goes against the normal lifecycle that's currently assumed.

All that the app currently does is use a text-to-speech library to speak numbers
starting at 1 and counting up indefinitely. A custom-drawn notification with
play/pause controls appears in the notification shade and can be used to pause
and resume the audio playback. The code is factored so that as much logic as
possible is done in JavaScript.

Ideally, some parts of this app will eventually be pulled out into libraries.