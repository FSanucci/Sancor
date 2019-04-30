$(document).ready(function() {
  $('#promotor-habilitar').click(function(){
    $('input[type="checkbox"]:checked').parent().parent().parent().find('.item-icons').show();
    $('input[type="checkbox"]:checked').prop('checked', '');
  });
  $('#promotor-deshabilitar').click(function(){
    $('input[type="checkbox"]:checked').parent().parent().parent().find('.item-icons').hide();
    $('input[type="checkbox"]:checked').prop('checked', '');
  });
});
