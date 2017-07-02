	$(document).ready(function() {
		
		   $('.ui-menuitem-link').each(function(){
		       if(window.location.pathname.indexOf($(this).attr('href')) != -1) {
		           $(this).css('background', 'white');//or add class
		       }
		   });  
		
		})