
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

  // filtros
  $('body').on('click', '#sin-agendar', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .sin-agendar').show();
    $('.item[data-filter="sin-agendar"]').show();
  });

  $('body').on('click', '#entrevistados', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .entrevistados').show();
    $('.item[data-filter="entrevistados"]').show();
  });

  $('body').on('click', '#cotizados', function (e) {
    e.preventDefault();
    $('.item').hide();
    $('.filtro > div').hide();
    $('.filtro .cotizados').show();
    $('.item[data-filter="cotizados"]').show();
  });

  $('body').on('click', '.filtro .cerrar', function () {
    $('.item').show();
    $(this).parent().hide();
  });

  // enviar form de carga de asociado
  $('body').on('click', '.cargar .btn-enviar', function (e) {
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
      $('.btn.cotizar').hide();
      $('.btn.modificar').show();
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
