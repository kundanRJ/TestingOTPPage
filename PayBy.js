let finalInput = "";

function body() {
 this.frm = document.getElementsByName("form1")[0];
  const input = document.querySelectorAll(".input");
  const inputField = document.querySelector(".otpInput");
  const submitButton = document.getElementById("submit");
  let inputCount = 0;
  //frm.input1.focus(); 
  //input.focus()[0];
  //  Update input
  const updateInputConfig = (element, disabledStatus) => {
    //element.disabled = disabledStatus;
    if (!disabledStatus) {
      element.focus();
    } else {
      element.blur();
    }
  };

  input.forEach((element) => {
    element.addEventListener("keyup", (e) => {
      //debugger;
      e.target.value = e.target.value.replace(/[^0-9]/g, "");

      let { value } = e.target;

      if (value.length == 1) {
        updateInputConfig(e.target, true);
        if (inputCount <= 5 && e.key != "Backspace") {
          finalInput += value;
         
          if (inputCount < 5) {
            updateInputConfig(e.target.nextElementSibling, false);
          }
        }
        //validateOTP(finalInput);
        inputCount += 1;
      } else if (value.length == 0 && e.key == "Backspace") {
        finalInput = finalInput.substring(0, finalInput.length - 1);
        if (inputCount == 0) {
          updateInputConfig(e.target, false);
          return false;
        }

        updateInputConfig(e.target, true);
        e.target.previousElementSibling.value = "";
        updateInputConfig(e.target.previousElementSibling, false);
        inputCount -= 1;
      } else if (value.length > 1) {
        e.target.value = value.split("")[0];
      }
     // submitButton.disabled=false;
    });
  });

  window.addEventListener("keyup", (e) => {
    if (inputCount > 5) {
     // submitButton.classList.remove("hide");
      //submitButton.classList.add("show");
      //submitButton.disabled=false;
      if (e.key == "Backspace") {
        finalInput = finalInput.substring(0, finalInput.length - 1);
        updateInputConfig(inputField.lastElementChild, false);
        inputField.lastElementChild.value = "";
        inputCount -= 1;
        submitButton.classList.add(disabled);
      }
    }
  });
}


 function formvalid() { 
 
 const inputs = document.querySelectorAll('input[type="password"]');
        const values = [];
  
        inputs.forEach(input => {
          values.push(input.value);
        });           
        textOtp=values.join('');
        var otpField=document.getElementById("OTP");
        otpField.value=textOtp;
        var getOtp=document.getElementById("OTP").value;
        otp1 =getOtp;
        
        alert("OTP1 : "+otp1);
        
         if(otp1.length==0||otp1.length==null)
        {
        alert("Please Enter Your OTP !");
           return false;
         }  
         
        else if (otp1.length != 6) {
                alert('OTP length should be minimum 6');
                    return false;
                }
        else if (!(otp1.match(regexp1)))
          {
             alert("only numbers are allowed");
              return false;
          }
          else {

               // alert("else block");
		return true;
		}
                
        } 

  
function clickOnOtp()
{     
  document.getElementById("in").focus();
	
  		 var sheet = document.createElement('style');     
 		 sheet.innerHTML = "input {border: none; background-color: #f5f6f7; color: black}";
  		 document.body.appendChild(sheet);   
  		
  	         document.getElementById("flasher").style.visibility = "hidden";   
	
}   
//For afetr resend click flsher enable    
  function flashEnable()
    {
   //alert("RESEND )";
     setTimeout (function(){
        },60000);
        var countdownNum = 6;
        incTimer();
        function incTimer(){
        setTimeout (function(){
            if(countdownNum != 0){

            countdownNum--;
             document.getElementById("resend").style.visibility = "hidden";
    	     document.getElementById("flasher").style.visibility = "visible";   
    	     document.getElementById("flasher").style.marginTop="16px";	    
            document.getElementById('timer').innerHTML = countdownNum + ' seconds';          
            incTimer();
            }
        },1000);
       }    
    }  
 
  function flashEnable()
    {
    
     setTimeout (function(){
        },60000);
        var countdownNum = 60;
        incTimer();
        function incTimer(){
        setTimeout (function(){
            if(countdownNum != 0)
            {
              countdownNum--;
              document.getElementById("resend").style.visibility = "hidden";
    	      document.getElementById("flasher_timer").style.visibility = "visible";   
    	      document.getElementById("flasher").style.visibility = "hidden";	    
    	      document.getElementById("flasher").style.margin = "0px";
             document.getElementById('timer').innerHTML = countdownNum + ' seconds';          
             incTimer();
            }
            else
            {
            
             document.getElementById("resend").style.visibility = "visible";
             document.getElementById("flasher_timer").style.visibility = "hidden";   
            }
        },1000);
        }
     
    }
    
    function f1()
  {
        var otpField=document.getElementById("OTP");
        otpField.value="";
  }
  
  
ocument.getElementById("resend").addEventListener("click", function(event) {

             alert("inside event function...");   
            event.preventDefault();

            var url=document.getElementById("resend");
            var xhr = new XMLHttpRequest();
            xhr.open("get", url , true);
            alert("sent request to..." +url);
            
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    console.log(xhr.responseText);
                }
            };
            
            xhr.send();
    
        });
       

   
       
   
  
