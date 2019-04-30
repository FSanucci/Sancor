$(document).ready(function() {
  $('.imc button').on('click', function(e){
    e.preventDefault();
    var peso = $(this).parent().parent().find('.peso').val();
    var altura = $(this).parent().parent().find('.altura').val();
    if(peso >= 1 && altura >= 1){
      var res = peso/(altura*altura);
      if (res <= 17) {
          $(this).parent().parent().find('.total').val('Delgadez');
          $(this).parent().parent().find('.total').parent().removeClass('is-empty');
      }else if (res >= 34) {
          $(this).parent().parent().find('.total').val('Sobrepeso');
          $(this).parent().parent().find('.total').parent().removeClass('is-empty');
      }else{
        $(this).parent().parent().find('.total').val('Normal IMC OK');
        $(this).parent().parent().find('.total').parent().removeClass('is-empty');
      }
    }

  });
  $('.int-nuevo .arrow').click(function(){
    $(this).parent().toggleClass('open');
  });

  $('.inputfile').on('change', function(){
    $(this).parent().addClass('on-file');
    var file = $(this)[0].files[0];
    $(this).parent().find('label.title').hide();
    $(this).parent().find('label.name').show();
    $(this).parent().find('label.name').text(file.name);
    // console.log(file);
  });
  $('.input-group .remove').click(function(){
    $(this).parent().find('.inputfile').val('');
    $(this).parent().removeClass('on-file');
    $(this).parent().find('label.title').show();
    $(this).parent().find('label.name').hide();
    $(this).parent().find('label.name').text('');
  });

  var paso = 1;
  // $('.paso-1').hide();
  $('.paso-2').hide();
  $('.paso-3').hide();
  $('.paso-4').hide();
  $('.paso-5').hide();
  $('.paso-6').hide();
  $('.paso-7').hide();
  $('.paso-8').hide();

  $('#afiliacion button.next').click(function(){
    $('.pagination').find('.selected').next().addClass('selected');
    $('.pagination').find('.selected').first().removeClass('selected');
    paso++;
    $('.pasos').hide();
    $('.paso-'+paso).show();
    $("html, body").animate({ scrollTop: 0 }, "slow");
  });
  // Autonomo Individual
  $('#afiliacion.autonomo-individual button.next ,#afiliacion.autonomo-empresa button.next ,#afiliacion.desregulado-empresa button.next ,#afiliacion.monotributo-empresa button.next ,#afiliacion.monotributo-individual button.next').click(function(){
    if(paso >= 2){
      $('#afiliacion button.prev').show();
    }
    if(paso >= 7){
      $(this).hide();
    }
  });
  // Desregulado Individual
  $('#afiliacion.desregulado-individual button.next ').click(function(){
    if(paso >= 2){
      $('#afiliacion button.prev').show();
    }
    if(paso >= 8){
      $(this).hide();
    }
  });

  $('#afiliacion button.prev').click(function(){
    $('.pagination').find('.selected').prev().addClass('selected');
    $('.pagination').find('.selected').last().removeClass('selected');
    paso--;
    $('.pasos').hide();
    $('.paso-'+paso).show();
    $("html, body").animate({ scrollTop: 0 }, "slow");
  });
  // Autonomo Individual
  $('#afiliacion.autonomo-individual button.prev ,#afiliacion.autonomo-empresa button.prev ,#afiliacion.desregulado-empresa button.prev ,#afiliacion.monotributo-empresa button.prev, #afiliacion.monotributo-individual button.prev').click(function(){
    if(paso === 1){
      $(this).hide();
    }
    if(paso <= 6){
      $('#afiliacion button.next').show();
    }
  });
  // Desregulado Individual
  $('#afiliacion.desregulado-individual button.prev').click(function(){
    if(paso === 1){
      $(this).hide();
    }
    if(paso <= 7){
      $('#afiliacion button.next').show();
    }
  });

  // Checkbox paso 3
  $('.paso-3 input[type=radio]').on('change', function(){
    var check1 = $('.paso-3 input[name=efactura]:checked').val();
    var check2 = $('.paso-3 input[name=factura-domicilio]:checked').val();
    var email = $('#email').val();
    if(check1 === 'si' && email === ''){
      console.log('e-factura');
      $('#alert-efactura').modal('toggle');
    }
    if(check1 === 'no' && check2 === 'no'){
      console.log('se cumple');
      $('.datos-domicilio').show();
    }else{
      console.log('no se cumple');
      $('.datos-domicilio').hide();
    }
  });
  $('#alert-efactura .cerrar').click(function(){
    $('#email').focus();
  });

  $('select[name=forma-pago]').on('change', function(){
    tipoPago();
    var select = $(this).val().toLowerCase();
    $('.'+select).show();
  });
  function tipoPago(){
    $('.cbu, .tc, .pr, .ecr').hide();
  }
  tipoPago();
});
