document.querySelectorAll('input[type="file"].form-security__input').forEach((input) => {
    const wrapper = document.createElement('div')
    wrapper.className = 'file-input'

    const button = document.createElement('button')
    button.type = 'button'
    button.className = 'file-input__button'
    button.textContent = 'Выбрать файл'

    const name = document.createElement('span')
    name.className = 'file-input__name'
    name.textContent = input.files.length ? input.files[0].name : 'Файл не выбран'

    input.parentNode.insertBefore(wrapper, input)
    wrapper.appendChild(button)
    wrapper.appendChild(name)
    wrapper.appendChild(input)
    input.classList.add('file-input__native')

    button.addEventListener('click', () => input.click())
    input.addEventListener('change', () => {
        name.textContent = input.files.length ? input.files[0].name : 'Файл не выбран'
    })
})