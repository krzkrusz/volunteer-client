function sort() {
    var inputArr = randomArray(10000, 100000);

    var len = inputArr.length;
    var swapped;
    do {
        swapped = false;
        for (var i = 0; i < len; i++) {
            if (inputArr[i] > inputArr[i + 1]) {
                var tmp = inputArr[i];
                inputArr[i] = inputArr[i + 1];
                inputArr[i + 1] = tmp;
                swapped = true;
            }
        }
    } while (swapped);
    return inputArr;
}

function randomArray(length, max) {
    return Array.apply(null, Array(length)).map(function() {
        return Math.round(Math.random() * max);
    });
}

