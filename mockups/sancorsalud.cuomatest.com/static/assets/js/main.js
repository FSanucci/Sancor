$(document).ready(function() {

  $.material.init();
  $('[data-toggle="tooltip"]').tooltip();

  $("#dni").keypress(function(e) {
    // if (String.fromCharCode(e.keyCode).match(/[^0-9]/g)) return false;
    return isNumber(event, this);
  });

  $('.menu li.active a').on('click', function(e) {
    e.preventDefault();
  });
  $('.menu li a').on('click', function() {
    $('body').removeClass('open-menu');
    $('.bg-menu').removeClass('open');
  });


  $('.form-group select').on('change', function(){
    if($(this).val() !== ''){
      $(this).parent().addClass('selected');
    }else{
      if($(this).parent().hasClass('selected')){
        $(this).parent().removeClass('selected');
      }
    }
  });

  var alto = $(window).height();
  var ancho = $(window).width();
  $(window).resize(function() {
    alto = $(window).height();
    ancho = $(window).width();
  });
  $('.menu').css('height', alto - 56);

  $('.btn-nav').on('click', function() {
    $('body').toggleClass('open-menu');
    if ($('.bg-menu').hasClass('open')) {
      $('.bg-menu').removeClass('open');
    }else{
      $('.bg-menu').addClass('open');
    }

  });
  $('.bg-menu').on('click', function() {
    $('body').removeClass('open-menu');
    $(this).removeClass('open');
  });
  $('.smart-search').on('click', function() {
    $('.search').toggleClass('open-top');
    $('.search').toggle();
    $(this).toggleClass('open-top');
    $('.search input.smart').focus();
  });
  $('.search input').on('focus', function() {
    if (ancho >= 769) {
      $('header').addClass('open-search');
    }
  });
  $('.search input').on('blur', function() {
    $('header').removeClass('open-search');
    if (ancho <= 768) {
      $('.search').toggleClass('open-top');
      $('.search').toggle();
      $('.smart-search').toggleClass('open-top');
    }
  });

  // filtros
  $('#sin-agendar').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .sin-agendar').show();
    $('.item[data-filter="sin-agendar"]').show();
  });
  $('#entrevistados').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .entrevistados').show();
    $('.item[data-filter="entrevistados"]').show();
  });
  $('#cotizados').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .cotizados').show();
    $('.item[data-filter="cotizados"]').show();
  });
  $('#fichas-pendientes').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .fichas-pendientes').show();
    $('.item[data-filter="fichas-pendientes"]').show();
  });
  $('#fichas-corregir').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .fichas-corregir').show();
    $('.item[data-filter="fichas-corregir"]').show();
  });
  $('#fichas-terminadas').on('click', function(e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .fichas-terminadas').show();
    $('.item[data-filter="fichas-terminadas"]').show();
  });

  $('.filtro .cerrar').on('click', function() {
    $('.item').show();
    $(this).parent().hide();
  });

  // enviar form de carga de asociado
  $('#promotor-cargar .btn-enviar').on('click', function(e) {
    if ($('input[name="name"]').val() !== "" && $('input[name="dni"]').val() !== "" && $('input[name="fecha-nacimiento"]').val() !== "" && $('input[name="cod-postal"]').val() !== "") {
      e.preventDefault();
      console.log('form lleno');
      $('#form-dialog p.nombre').text($('input[name="name"]').val());
      $('#form-dialog p.dni').text('DNI: ' + $('input[name="dni"]').val());
      $('#form-dialog p.fecha-nacimiento').text('Fecha de Nacimiento: ' + $('input[name="fecha-nacimiento"]').val());
      $('#form-dialog p.cod-postal').text('Código Postal: ' + $('input[name="cod-postal"]').val());
      $('#form-dialog').modal('toggle');
    }
    // $('#form-dialog').modal('toggle');
  });
  // enviar form de carga de asociado SUPERVISOR
  $('#supervisor-cargar .btn-enviar').on('click', function(e) {
    if ($('input[name="name"]').val() !== "" && $('input[name="dni"]').val() !== "" && $('input[name="fecha-nacimiento"]').val() !== "" && $('input[name="cod-postal"]').val() !== "" && $('input[name="tel"]').val() !== "" && $('input[name="cel"]').val() !== "" && $('input[name="mail"]').val() !== "") {
      e.preventDefault();
      console.log('form lleno');
      $('#form-dialog p.nombre').text($('input[name="name"]').val());
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

  $('.item-text').on('click', function() {
    var modal = $(this).data("modalnum");
    var cotizado = $(this).data("cotizado");
    if(cotizado === true){
      $('#' + modal).find('.btn.cotizar').hide();
      $('#' + modal).find('.btn.ver-cotizacion').show();
    }else{
      $('#' + modal).find('.btn.cotizar').show();
      $('#' + modal).find('.btn.ver-cotizacion').hide();
    }
    $('#' + modal).find('.item').last().addClass('last');
    $('#' + modal).find('.plan').hide();
    $('#' + modal).find('.costo').hide();

    $('#' + modal + ' .modal-header > h3').text($(this).find('.nombre').text());
    $('#' + modal).modal('toggle');
    console.log($(this).find('.nombre').text());
  });

  $('.cotizar').on('click', function(e) {
    e.preventDefault();
    $('.modal.asignados').modal('toggle');
    $('#cotizar').modal('toggle');
  });
  $('#asignado1 .ver-cotizacion').on('click', function(e) {
    e.preventDefault();
    window.location.href = "recotizar.html";
  });
  $('#asignado1 .cargar').on('click', function(e) {
    e.preventDefault();
    window.location.href = "inicio-de-carga.html";
  });


  $('#cotizar .btn').click(function(){
    var url = $(this).data('form');
  });

  $('.cotizar-ok').on('click', function() {
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
      $('.btn.cotizar').hide();
      $('.btn.modificar').show();
    }
    $('.cotizar-modal').hide();
    $('.modal-content.open-modal').toggleClass('open-modal');
  });

  $('.cotizar-modal .cerrar').on('click', function(e) {
    e.preventDefault();
    $('.cotizar-modal').hide();
    $('.modal-content.open-modal').toggleClass('open-modal');
  });

  $('.modal .cerrar').on('click', function() {
    $('.modal').modal('hide');
  });
  $('#form-dialog .enviar').on('click', function() {
    console.log('Envio Datos');
  });

  // Mensaje supervisor sin asignar
  function cantidad_sin_asignar() {
    var not_check = $('.item-list').find('input[name="asignar[]"]:not(:checked)').length;
    var texto = "Estéban, tenes " + not_check + " potenciales Asociados sin asignar";
    $('.supervisor-mensaje').text(texto);
    $('#confirmar').removeClass('open');
  }
  cantidad_sin_asignar();

  // Checkbox supervisor asignar
  $('#supervisor .checkbox input').on('change', function() {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var user = $('input[name="usuario"]:checked').data('name');
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
  $('.btn-back').click(function(event) {
    $('.btn-nav').show();
    $('#lista').show();
    $('#lista-promotor').hide();
    $('.btn-back').hide();
    $('#confirmar .step2').hide();
    $('#confirmar .step1').show();
  });
  $('#confirmar .step1').click(function(event) {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var texto = "Vas a asignar " + check + " Potenciales Asociados a";
    $('.btn-nav').hide();
    $('#lista').hide();
    $('#lista-promotor').show();
    $('.btn-back').show();
    $('#confirmar .step1').hide();
    $('#confirmar .step2').show();
  });
  $('#confirmar .step2').click(function(event) {
    var check = $('.item-list').find('input[name="asignar[]"]:not(:disabled):checked').length;
    var user = $('input[name="usuario"]:checked').data('name');
    var message = "Vas a asignar " + check + " potenciales Asociados a " + user;
    console.log(message);
    $('#asignar p').text(message);
    $('#asignar').modal('toggle');
  });

  $('#cotizar.modal .btn').click(function(){
    var url = $(this).data('form');
    window.location.href = "cotizar.html?tipo="+url;
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
