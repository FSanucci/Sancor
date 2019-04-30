$(document).ready(function() {
  // GET VALUE
  var $_GET = {};
  document.location.search.replace(/\??(?:([^=]+)=([^&]*)&?)/g, function() {
    function decode(s) {
      return decodeURIComponent(s.split("+").join(" "));
    }

    $_GET[decode(arguments[1])] = decode(arguments[2]);
  });
  if($_GET.tipo == 'plan'){
    $('#plan').show();
  }
  if($_GET.tipo == 'adicional'){
    $('#adicional').show();
    $('.btn-ver').show();
    $('.next').hide();
  }
  if($_GET.tipo == 'transferencia'){
    $('#transferencia').show();
    $('.next .button.next').addClass('transferencia');
    $('.next .button.prev').addClass('transferencia-prev');
    $('.next .button.next').removeClass('next');
    $('.next .button.prev').removeClass('prev');
  }
  if($_GET.tipo == 'manual'){
    $('#cotizar-modal').modal('toggle');
    $('#cotizar-modal h5').text('Cotización manual');
    $('#cotizar-modal input').prop('disabled', '');
    $('#cotizar-modal input').attr('placeholder', '$');
  }
  if($_GET.tipo == 'solo-adicionales'){
    $('#adicional').show();
    $('#adicional .step-1').show();
    $('#adicional .step-2').hide();
    $('.next .button.next').addClass('solo-adicional');
    $('.next .button.prev').addClass('solo-adicional-prev');
    $('.next .button.next').removeClass('next');
    $('.next .button.prev').removeClass('prev');
  }



  $('#sumar-integrante').click(function(){
    if($('#parent').val() !== '' && $('#edad').val() !== '' && $('#dni-int').val() !== ''){
      var parent = $('#parent').val();
      var edad = $('#edad').val();
      var dni = $('#dni-int').val();
      var texto = parent+', '+edad+' años, DNI:'+dni;
      $('#integrante p.integrante').text(texto);
      $('#integrante').modal('toggle');
    }else{
      // console.log('noooooo'); ERROR MENSAJE
    }
  });
  var intNum = 1;
  $('#acepta-integrante').click(function(e) {
    e.preventDefault();
    var integrante = $('#integrante p.integrante').text();
    var text = "<div id=int"+intNum+" class='int-nuevo'><i class='material-icons'>person</i><p>"+integrante+"</p><div class='clearfix'></div></div>";
    $('#integrantes-nuevos').html(text);
    $('#edad').val("");
    $('#dni-int').val("");
    $('#parent').val("");
    $('#parent').parent().removeClass('selected');
    $('#integrante').modal('toggle');
    intNum++;
  });

  $('select[name=ingreso]').on('change', function(){
    if($(this).val() === 'empresa'){
      $('.numero-ingreso').show();
      $('input[name="numero-ingreso"]').parent().find('label').text('Número o nombre de empresa');
    }else if ($(this).val() === 'grupo') {
      $('input[name="numero-ingreso"]').parent().find('label').text('Número o nombre de afinidad');
    }else{
      $('.numero-ingreso').hide();
    }
  });

  // PASO 2

  $('.btn.next').click(function(){
    var segmento = $('select.segmento').val();
    var ingreso = $('select.ingreso').val();
    if(segmento !== '' && ingreso !== ''){
      $(this).hide();
      $('.btn.prev').show();
      $('.btn-ver').show();
      $('.pagination .dot').toggleClass('selected');
      if(segmento == 'autonomo'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
      }
      if(segmento == 'desregulado'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
        $('#desregulado').show();
      }
      if(segmento == 'monotributista'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
        $('#monotributo').show();
      }
    }

  });
  $('.btn.prev').click(function(){
    $(this).hide();
    $('.btn.next').show();
    $('.btn-ver').hide();
    $('.pagination .dot').toggleClass('selected');
    $('#step-2 .form-group').hide();
    $('#step-2').hide();
    $('#plan').slideDown();
    $('#desregulado').hide();
    $('#monotributo').hide();
  });

  $('#step-2 .pago').change(function(){
    var pago = $(this).val();
    if(pago == 'TC'){
      $('#step-2 #tarjeta').prop('disabled', '');
      $('#step-2 #tarjeta').parent().removeClass('disabled');
    }else{
      $('#step-2 #tarjeta').prop('disabled', 'disabled');
      $('#step-2 #tarjeta').parent().addClass('disabled');
    }
    if(pago == 'TC' || pago == 'CBU'){
      $('#step-2 #banco').prop('disabled', '');
    }else{
      $('#step-2 #banco').prop('disabled', 'disabled');
    }

  });

  $('#desregulado .calculo').change(function(){
    var option = $(this).val();
    console.log(option);
    if(option == 'Aporte obra social'){
      $('#desregulado .calc-aporte').hide();
      $('#desregulado .calc-remuneracion').show();
      $('#desregulado .calc').show();
      $('#desregulado .calc').removeClass('is-empty');
    }
    if(option == 'Remuneración bruta'){
      $('#desregulado .calc-aporte').show();
      $('#desregulado .calc-remuneracion').hide();
      $('#desregulado .calc').show();
      $('#desregulado .calc').removeClass('is-empty');
    }
  });
  // transferencia next
  $('.button.transferencia').click(function(){
    var segmento = $('select[name=segmento-nuevo]').val();
    var ingreso = $('select[name=ingreso-nuevo]').val();
    if(segmento !== '' && ingreso !== ''){
      $(this).hide();
      $('.button.transferencia-prev').show();
      $('.btn-ver').show();
      $('.pagination .dot').toggleClass('selected');
      if(segmento == 'autonomo'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
        $('#transferencia').slideUp();
      }
      if(segmento == 'desregulado'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
        $('#transferencia').slideUp();
        $('#desregulado').show();
      }
      if(segmento == 'monotributista'){
        $('#ingreso .form-group').hide();
        $('#plan').slideUp();
        $('#step-2').show().delay(400);
        ingresoTipo(ingreso);
        $('#transferencia').slideUp();
        $('#monotributo').show();
      }
    }
  });
  $('.button.transferencia-prev').click(function(){
      $(this).hide();
      $('.button.transferencia').show();
      $('.btn-ver').hide();
      $('.pagination .dot').toggleClass('selected');
      $('#step-2 .form-group').hide();
      $('#step-2').hide();
      $('#transferencia').slideDown();
      $('#desregulado').hide();
      $('#monotributo').hide();
  });

  $('.button.solo-adicional').click(function(){
    $(this).hide();
    $('#adicional .step-1').hide();
    $('#adicional .step-2').show();
    $('.btn-ver').show();
    $('.button.solo-adicional-prev').show();
  });
  $('.button.solo-adicional-prev').click(function(){
    $(this).hide();
    $('.btn-ver').hide();
    $('.pagination .dot').toggleClass('selected');
    $('.button.solo-adicional').show();
    $('#adicional .step-1').show();
    $('#adicional .step-2').hide();
    $('.btn-ver').hide();
  });

  // MODAL TOGGLES
  // $('#cotizar-modal').modal('toggle'); // ELIMINAR

  $('#no-adicionales').click(function(){
    $('#cotizacion-fin').modal('hide')
            .on('hidden.bs.modal', function (e) {
                $('#cotizar-modal').modal('show');
                $(this).off('hidden.bs.modal'); // Remove the 'on' event binding
            });

  });

  $('#ver-cotizacion').click(function(){
    $('#cotizacion-fin').modal('toggle');
  });

  $('#cancela-integrante').click(function(){
    $('#integrante').modal('toggle');
  });

  $('#cotizar-modal .arrow').click(function(){
    $(this).parent().toggleClass('open');
  });

  function ingresoTipo(tipo){
    switch(tipo) {
      case 'individual':
      $('#ingreso .form-group.individual').show();
      break;
      case 'empresa':
      // $('#ingreso input[name="numero"]').parent().find('label').text('Número o nombre de empresa');
      $('#ingreso .form-group.empresa').show();
      break;
      case 'grupo':
      // $('#ingreso input[name="numero"]').parent().find('label').text('Número o nombre de afinidad');
      $('#ingreso .form-group.grupo').show();
      break;
      default:
      console.log('nada');
    }
  }

  // RECOTIZAR
  $('#recotizar .btn-cal').click(function(){
    console.log('recotizar');
    window.location.href = "cotizar.html?tipo=plan";
  });

  // $("input[required=required]").on("invalid", function () {
  //   this.setCustomValidity("Este dato es obligatorio");
  // });

});



