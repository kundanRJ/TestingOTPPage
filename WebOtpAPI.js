export function autoReadSMS(cb) {
  // Used AbortController with setTimeout so that WebOTP API (Autoread sms) will get disabled after 1 minute
  const signal = new AbortController();
  setTimeout(() => {
    signal.abort();
  }, 1 * 60 * 1000);

  async function main() {
    if ('OTPCredential' in window) {
      try {
        if (navigator.credentials) {
          try {
            await navigator.credentials
              .get({ abort: signal, otp: { transport: ['sms'] } })
              .then(content => {
                if (content && content.code) {
                  const otpRegex = /OTP:(\d{4})/; // Regex to match OTP format
                  const match = content.code.match(otpRegex);
                  if (match && match[1]) {
                    cb(match[1]); // Pass the extracted OTP to the callback function
                  }
                }
              })
              .catch(e => console.log(e));
          } catch (e) {
            return;
          }
        }
      } catch (err) {
        console.log(err);
      }
    }
  }
  main();
}
