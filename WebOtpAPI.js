navigator.credentials.get({
  otp: { transport: ['sms'] }
})
.then(otp => document.getElementById('otp-input').value = otp.code);
