$(document).ready(function () {
  $.material.init();
  $("#dni").keypress(function (e) {
    // if (String.fromCharCode(e.keyCode).match(/[^0-9]/g)) return false;
    return isNumber(event, this);
  });

  $('body').on('click', '.menu li.active a', function (e) {
    e.preventDefault();
  });

  var alto = $(window).height();
  var ancho = $(window).width();
  $(window).resize(function () {
    alto = $(window).height();
    ancho = $(window).width();
  });
  $('body').on('click', '.btn-menu', function (){
    $(".header .menu").css('height', alto - 56);
  });

  $('body').on('click', '.btn-nav', function () {

    $('body').toggleClass('open-menu');
    $('.bg-menu').toggleClass('open');
  });

  $('body').on('click', '.bg-menu', function () {
    $('body').toggleClass('open-menu');
    $(this).toggleClass('open');
  });
  $('body').on('click', '.smart-search', function () {
    $('.search').toggleClass('open-top');
    $('.search').toggle();
    $(this).toggleClass('open-top');
    $('.search input.smart').focus();
  });

  $('body').on('focus', '.search input', function () {
    if (ancho >= 769) {
      $('.header').addClass('open-search');
    }
  });
  $('body').on('blur', '.search input', function () {
    $('.header').removeClass('open-search');
    if (ancho <= 768) {
      $('.search').toggleClass('open-top');
      $('.search').toggle();
      $('.smart-search').toggleClass('open-top');
    }
  });


  $('body').on('click', '#dropdownMenu1', function () {
    if ($('.filtros.dropdown').hasClass("open")){
      $('.filtros.dropdown').removeClass("open")
    }else{
      $('.filtros.dropdown').addClass("open")
    }

  });



  // filtros
  $('body').on('click', '#sin-agendar', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.modal.asignados .item').show();
    $('.filtro > div').hide();
    $('.filtro .sin-agendar').show();
    $('.item[data-filter="S"]').show();
  });

  $('body').on('click', '#entrevistados', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.modal.asignados .item').show();
    $('.filtro > div').hide();
    $('.filtro .entrevistados').show();
    $('.item[data-filter="A"]').show();
  });

  $('body').on('click', '#cotizados', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.modal.asignados .item').show();
    $('.filtro > div').hide();
    $('.filtro .cotizados').show();
    $('.item[data-filter="C"]').show();
  });

  $('body').on('click', '.filtro .cerrar', function () {
    $('.item').show();
    $(this).parent().hide();
  });

  // enviar form de carga de asociado
  $('body').on('click', '.cargar .btn-enviar', function (e) {
    if ($('input[name="name"]').val() !== "" && $('input[name="dni"]').val() !== ""  && ($('input[name="tel"]').val() !== "" || $('input[name="cel"]').val() !== "") ) {
      e.preventDefault();
      console.log('form lleno');
      $('#form-dialog p.nombre').text($('input[name="name"]').val() + " " + $('input[name="apellido"]').val());
      $('#form-dialog p.dni').text('DNI: ' + $('input[name="dni"]').val());
      $('#form-dialog p.fecha-nacimiento').text('Fecha de Nacimiento: ' + $('input[name="fecha-nacimiento"]').val());
      $('#form-dialog p.cod-postal').text('Código Postal: ' + $('input[name="cod-postal"]').val());
      $('#form-dialog p.tel').text('Teléfono fijo: ' + $('input[name="tel"]').val());
      $('#form-dialog p.cel').text('Teléfono celular: ' + $('input[name="cel"]').val());
      $('#form-dialog p.mail').text('Mail: ' + $('input[name="mail"]').val());
      $('#form-dialog').modal('toggle');
    }
    // $('#form-dialog').modal('toggle');
  });
  // enviar form de carga de asociado SUPERVISOR
  $('#supervisor-cargar .btn-enviar').on('click', function (e) {
    if ($('input[name="name"]').val() !== "" && $('input[name="dni"]').val() !== ""  && ($('input[name="tel"]').val() !== "" || $('input[name="cel"]').val() !== "") ) {
      e.preventDefault();
      console.log('form lleno');
      $('#form-dialog p.nombre').text($('input[name="name"]').val() + " " + $('input[name="apellido"]').val());
      $('#form-dialog p.dni').text('DNI: ' + $('input[name="dni"]').val());
      $('#form-dialog p.fecha-nacimiento').text('Fecha de Nacimiento: ' + $('input[name="fecha-nacimiento"]').val());
      $('#form-dialog p.cod-postal').text('Código Postal: ' + $('input[name="cod-postal"]').val());
      $('#form-dialog p.tel').text('Teléfono fijo: ' + $('input[name="tel"]').val());
      $('#form-dialog p.cel').text('Teléfono celular: ' + $('input[name="cel"]').val());
      $('#form-dialog p.mail').text('Mail: ' + $('input[name="mail"]').val());
      $('#form-dialog').modal('toggle');
    }
    // $('#form-dialog').modal('toggle');
  });

  $('body').on('click', '.item-text', function () {
    var modal = $(this).data("modalnum");
    $('#' + modal).find('.item').last().addClass('last');
    $('#' + modal).find('.plan').hide();
    $('#' + modal).find('.costo').hide();

    $('#' + modal + ' .modal-header > h3').text($(this).find('.nombre').text());
    $('#' + modal).modal('toggle');
    console.log($(this).find('.nombre').text());
  });

  $('body').on('click', '.cotizar', function (e) {
    e.preventDefault();
    $(this).parent().parent().find('.cotizar-modal .name').text($(this).parent().parent().parent().parent().find('.modal-header h3').text());
    $(this).parent().parent().parent().parent().find('.modal-content').toggleClass('open-modal');
    $(this).parent().parent().find('.cotizar-modal').show();
  });

  $('body').on('click', '.cotizar-ok', function () {
    var plan = $(this).parent().find('input[name=plan-value]').val();
    var costo = $(this).parent().find('input[name=costo-value]').val();
    if (plan !== '') {
      $(this).parent().parent().find('.form-group.plan input').prop("value", plan);
      $(this).parent().parent().find('.form-group.plan').removeClass('is-empty');
      $(this).parent().parent().find('.form-group.plan').show();
      $(this).parent().parent().find('.icono.plan').show();
    }
    if (costo !== '') {
      $(this).parent().parent().find('.form-group.costo input').prop("value", costo);
      $(this).parent().parent().find('.form-group.costo').removeClass('is-empty');
      $(this).parent().parent().find('.form-group.costo').show();
      $(this).parent().parent().find('.icono.costo').show();
    }

    if (costo !== '' && plan !== '') {
      //$('.btn.cotizar').hide();
      //$('.btn.modificar').show();
    }
    $('.cotizar-modal').hide();
    $('.modal-content.open-modal').toggleClass('open-modal');
  });

  $('body').on('click', '.cotizar-modal .cerrar', function (e) {
    e.preventDefault();
    $('.cotizar-modal').hide();
    $('.modal-content.open-modal').toggleClass('open-modal');
  });

  $('body').on('click', '#form-dialog .cerrar', function () {
    $('#form-dialog').modal('toggle');
  });
  $('body').on('click', '#form-dialog .enviar', function () {
    console.log('Envio Datos');
  });



  // Mensaje supervisor sin asignar
  function cantidad_sin_asignar() {
    var not_check = $('.item-list').find('input[name="asignar[]"]:not(:checked)').length;
    var texto = "Tienes " + not_check + " potenciales Asociados sin asignar";
    $('.supervisor-mensaje').text(texto);
    $('#confirmar').removeClass('open');
  }

  cantidad_sin_asignar();

  // Checkbox supervisor asignar
  $('body').on('change', '.checkbox input', function (e) {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var user = $('input[name="usuario"]:checked').val();
    console.log(user);

    if (check >= 1) {
      var texto = "Vas a asignar " + check + " potenciales Asociados";
      if (user !== undefined) {
        texto = "Vas a asignar " + check + " potenciales Asociados a " + user;
      }
      $('.supervisor-mensaje').text(texto);
      $('#confirmar').addClass('open');
    } else {
      cantidad_sin_asignar();
    }
  });

  $('body').on('click', '.btn-back', function (e) {
  //$('.btn-back').click(function (event) {
    $('.btn-nav').show();
    $('#lista').show();
    $('#lista-promotor').hide();
    $('.btn-back').hide();
    $('#confirmar .step2').hide();
    $('#confirmar .step1').show();
  });

  $('body').on('click', '#confirmar .step1', function (e) {
  //$('#confirmar .step1').click(function (event) {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var texto = "Vas a asignar " + check + " Potenciales Asociados a";
    $('.btn-nav').hide();
    $('#lista').hide();
    $('#lista-promotor').show();
    $('.btn-back').show();
    $('#confirmar .step1').hide();
    $('#confirmar .step2').show();
    $('html, body').animate({ scrollTop: 0 }, 'fast');
  });
  $('body').on('click', '#confirmar .step2', function (e) {
  //$('#confirmar .step2').click(function (event) {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var user = $('input[name="usuario"]:checked').val();
    var message = "Vas a asignar " + check + " potenciales Asociados a " + user;
    console.log(message);
    $('#asignar p').text(message);
    $('#asignar').modal('toggle');
  });

});

// THE SCRIPT THAT CHECKS IF THE KEY PRESSED IS A NUMERIC OR DECIMAL VALUE.
function isNumber(evt, element) {

  var charCode = (evt.which) ? evt.which : event.keyCode;
  // console.log(charCode);

  if (
    // (charCode != 46 || $(element).val().indexOf('.') != -1) && // “.” CHECK DOT, AND ONLY ONE.
    (charCode < 46 || charCode > 57))
    return false;

  return true;
}



