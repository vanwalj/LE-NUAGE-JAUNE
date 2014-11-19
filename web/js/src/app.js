/**
 * Created by Jordan on 11/19/2014.
 */
if (navigator.webkitGetUserMedia) {
    navigator.webkitGetUserMedia({video: true, audio: true}, function (mediaStream) {
        document.querySelector('video').src(window.webkitURL.createObjectURL(mediaStream));
    }, function (err) {
        console.log("Webkit maggle", err);
    });
}