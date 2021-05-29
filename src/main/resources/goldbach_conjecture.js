function goldbach(n) {
    // get input
    //var n = document.getElementById("number").value;
    // validate input
    if (n != parseInt(n) || n <= 2 || n % 2 != 0) {
        alert("Invalid input! Enter even integer greater than 2.");
    } else {
        /*
          generate all primes below (or equal to) input number (sieve of eratosthenes);
          return array of those primes;
        */
        function getPrimes(n) {
            var sieve = [],
                i, j, primes = [];
            for (i = 2; i <= n; ++i) {
                if (!sieve[i]) {
                    primes.push(i);
                    for (j = i << 1; j <= n; j += i) {
                        sieve[j] = true;
                    }
                }
            }
            return primes;
        }
        // generate array of all possible tupples
        var primes = getPrimes(n);
        var tupples = [];
        var len = primes.length;
        for (i = 0; i < len; i++) {
            for (j = len - 1; j >= i; j--) {
                if (primes[i] + primes[j] == n) {
                    tupples.push(primes[i] + " + " + primes[j]);
                    break;
                }
            }
            if (primes[i] + primes[j] === n) {
                break;
            }
        }
        //return "Goldbach result:<br>" + n + " = " + tupples.join(" = ");
        return tupples.length.valueOf() > 0;
    };
}